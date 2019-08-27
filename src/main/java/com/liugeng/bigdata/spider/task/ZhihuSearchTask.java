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
import com.liugeng.bigdata.spider.output.impl.ZhihuDataEsOutput;
import com.liugeng.bigdata.spider.output.impl.ZhihuImageLocalOutput;
import com.liugeng.bigdata.spider.parser.AsyncParser;
import com.liugeng.bigdata.spider.parser.ZhihuSearchParser;
import com.liugeng.bigdata.spider.utils.CommonUtils;
import com.liugeng.bigdata.spider.utils.SpringBeanUtils;
import com.liugeng.bigdata.spider.utils.ZhihuSpiderUtils;
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
public class ZhihuSearchTask extends FileStoreTask {
	
	@Value("${spider.zhihu.search-word}")
	private String searchWord;
	
	@Override
	public ReturnT<String> execute(String params) {
		XxlJobLogger.log("开始知乎爬虫");
		Map<String, String> paramMap = CommonUtils.parseStringParams(params);
		XxlJobLogger.log("爬虫参数：" + paramMap);
		String searchWord = MapUtils.getString(paramMap, "searchWord", this.searchWord);
		Map<String, String> searchParams = ZhihuSpiderUtils.buildParams(searchWord, 20, 0);
		RunData runData = new LocalRunData();
		runData.addUrl(ZHIHU_SEARCH_API_BASE);
		ZhihuSearchParser parser = getJsonPageParser(paramMap, runData);
		DataOutput<List<DataDto>> output = chooseOutput(paramMap);
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
		return ReturnT.SUCCESS;
	}
	
	private DataOutput<List<DataDto>> chooseOutput(Map<String, String> paramMap) {
		String outType = MapUtils.getString(paramMap, "outType");
		String taskType = MapUtils.getString(paramMap, "type");
		switch (taskType) {
			case "image":
				String fileBasePath = MapUtils.getString(paramMap, "fileBasePath");
				Preconditions.checkNotNull(fileBasePath, "fileBasePath should not be null when outType is local.");
				ZhihuImageLocalOutput localOutput = (ZhihuImageLocalOutput) outputMap.get(outType);
				// TODO: 2019/8/27
				localOutput.setUri(fileBasePath);
				return localOutput;
			default:
				return  (ZhihuDataEsOutput) outputMap.get("es");
		}
	}
	
	@Override
	protected Map<String, DataOutput> initOutputList() {
		ZhihuImageLocalOutput localOutput = SpringBeanUtils.getBean("zhihuImageLocalOutput", ZhihuImageLocalOutput.class);
		ZhihuDataEsOutput esOutput = SpringBeanUtils.getBean("zhihuDataEsOutput", ZhihuDataEsOutput.class);
		localOutput.setAsyncOutputWorkers(asyncOutputWorkers);
		Map<String, DataOutput> outputMap = new HashMap<>();
		outputMap.put("local", localOutput);
		outputMap.put("es", esOutput);
		return outputMap;
	}
	
	@Override
	public void stopTask() {
		asyncOutputWorkers.shutdownNow();
	}
}
