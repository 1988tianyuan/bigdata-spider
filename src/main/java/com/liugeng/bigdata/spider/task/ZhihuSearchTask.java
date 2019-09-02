package com.liugeng.bigdata.spider.task;

import static com.liugeng.bigdata.spider.common.Constants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Preconditions;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.output.DataOutput;
import com.liugeng.bigdata.spider.output.OutputType;
import com.liugeng.bigdata.spider.output.impl.ZhihuDataEsOutput;
import com.liugeng.bigdata.spider.output.impl.ZhihuImageLocalOutput;
import com.liugeng.bigdata.spider.parser.AsyncParser;
import com.liugeng.bigdata.spider.parser.ZhihuSearchParser;
import com.liugeng.bigdata.spider.utils.CommonUtils;
import com.liugeng.bigdata.spider.utils.SpringBeanUtils;
import com.liugeng.bigdata.spider.utils.ZhihuSpiderUtils;
import com.liugeng.bigdata.spider.web.request.ZhihuSpiderTaskRequest;
import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.rundata.RunData;
import com.xuxueli.crawler.rundata.strategy.LocalRunData;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 16:03
 */
@JobHandler("zhihuSearchImageTask")
@Getter
@Setter
@ToString
public class ZhihuSearchTask extends ZhihuSpiderTask {
	
	@Value("${spider.zhihu.search-word}")
	private String searchWord;
	
	@Override
	public ReturnT<String> execute(String params) {
		XxlJobLogger.log("开始知乎爬虫");
		Map<String, String> paramMap = CommonUtils.parseStringParams(params);
		XxlJobLogger.log("爬虫参数：" + paramMap);
		innerExecute(createRequest(paramMap));
		return ReturnT.SUCCESS;
	}
	
	public boolean innerExecute(ZhihuSpiderTaskRequest request) {
		String searchWord = request.getKeyWord();
		Map<String, String> searchParams = ZhihuSpiderUtils.buildParams(searchWord, 20, 0);
		RunData runData = new LocalRunData();
		runData.addUrl(ZHIHU_SEARCH_API_BASE);
		ZhihuSearchParser parser = getJsonPageParser(request.getTaskType(), runData);
		DataOutput<List<DataDto>> output = chooseOutput(request);
		parser.setDataOutput(output);
		@Cleanup("stop")
		XxlCrawler crawler = new XxlCrawler.Builder()
			.setRunData(runData)
			.setPageParser(parser)
			.setParamMap(searchParams)
			.setFailRetryCount(3)
			.build();
		crawler.start(true);
		if (ClassUtils.isAssignable(parser.getClass(), AsyncParser.class)) {
			((AsyncParser)parser).syncAwait(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
		}
		return true;
	}
	
	private DataOutput<List<DataDto>> chooseOutput(ZhihuSpiderTaskRequest request) {
		TaskType taskType = request.getTaskType();
		OutputType outputType = request.getOutputType();
		DataOutput<List<DataDto>> dataOutput;
		switch (taskType) {
			case IMAGE:
				String fileBasePath =request.getFileBasePath();
				Preconditions.checkNotNull(fileBasePath, "fileBasePath should not be null when outType is local.");
				ZhihuImageLocalOutput localOutput = (ZhihuImageLocalOutput) outputMap.get(outputType.getType());
				// TODO: 2019/8/27
				localOutput.setUri(fileBasePath);
				dataOutput = localOutput;
				break;
			default:
				dataOutput = (ZhihuDataEsOutput) outputMap.get("es");
		}
		Preconditions.checkNotNull(dataOutput, "Your specified output type doesn't exist, please confirm the params.");
		return dataOutput;
	}
	
	@Override
	protected Map<String, DataOutput> initOutputList() {
		ZhihuImageLocalOutput localOutput = SpringBeanUtils.getBean("zhihuImageLocalOutput", ZhihuImageLocalOutput.class);
		localOutput.setAsyncOutputWorkers(asyncOutputWorkers);
		ZhihuDataEsOutput esOutput = SpringBeanUtils.getBean("zhihuDataEsOutput", ZhihuDataEsOutput.class);
		Map<String, DataOutput> outputMap = new HashMap<>();
		outputMap.put("local", localOutput);
		outputMap.put("es", esOutput);
		return outputMap;
	}
	
	@Override
	public void stopTask() {
		asyncOutputWorkers.shutdownNow();
	}
	
	private ZhihuSpiderTaskRequest createRequest(Map<String, String> paramMap) {
		String searchWord = MapUtils.getString(paramMap, "searchWord", this.searchWord);
		String outType = MapUtils.getString(paramMap, "outType", "es");
		String taskType = MapUtils.getString(paramMap, "type", "data");
		boolean includeAll = MapUtils.getBoolean(paramMap, "includeAll", false);
		String fileBasePath = MapUtils.getString(paramMap, "fileBasePath", "false");
		return new ZhihuSpiderTaskRequest(searchWord, includeAll, OutputType.valueOf(outType), TaskType.valueOf(taskType),
			fileBasePath);
	}
}
