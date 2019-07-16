package com.liugeng.bigdata.spider.task;

import static com.liugeng.bigdata.spider.common.Constants.*;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import com.liugeng.bigdata.spider.output.FileOutput;
import org.apache.commons.collections4.MapUtils;

import com.liugeng.bigdata.spider.output.ZhihuDataOutPut;
import com.liugeng.bigdata.spider.output.impl.ZhihuImageLocalOutput;
import com.liugeng.bigdata.spider.output.impl.ZhihuKafkaOutput;
import com.liugeng.bigdata.spider.parser.ZhihuSearchImageParser;
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
public class ZhihuSearchImageTask extends FileStoreTask {
	
	private String searchWord;
	private ForkJoinPool asyncOutputWorkers;
	private int workers;
	
	@Override
	public ReturnT<String> execute(String params) {
		XxlJobLogger.log("开始知乎爬图");
		Map<String, String> paramMap = CommonUtils.parseStringParams(params);
		XxlJobLogger.log("爬图参数：" + paramMap);
		String searchWord = MapUtils.getString(paramMap, "searchWord", this.searchWord);
		Map<String, String> searchParams = ZhihuSpiderUtils.buildParams(searchWord, 20, 0);
		RunData runData = new LocalRunData();
		runData.addUrl(ZHIHU_SEARCH_API_BASE);
		ZhihuSearchImageParser parser = (ZhihuSearchImageParser)getJsonPageParser("zhihuSearchImageParser", runData);
		FileOutput output = chooseOutput(paramMap);
		parser.setDataOutput(output);
		@Cleanup("stop")
		XxlCrawler crawler = new XxlCrawler.Builder()
			.setRunData(runData)
			.setPageParser(parser)
			.setParamMap(searchParams)
			.setFailRetryCount(3)
			.build();
		crawler.start(true);
		parser.syncAwait(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
		return ReturnT.SUCCESS;
	}
	
	public void initTask() {
		asyncOutputWorkers = (ForkJoinPool)Executors.newWorkStealingPool(workers);
	}

	@Override
	public FileOutput chooseOutput(Map<String, String> paramMap) {
		String fileBasePath = MapUtils.getString(paramMap, "fileBasePath");
		String type = MapUtils.getString(paramMap, "outType");
		switch (type) {
			default:
				ZhihuImageLocalOutput localOutput = SpringBeanUtils.getBean("zhihuImageLocalOutput", ZhihuImageLocalOutput.class);
				localOutput.setAsyncOutputWorkers(asyncOutputWorkers);
				if (fileBasePath != null) {
					localOutput.setFileBasePath(fileBasePath);
				}
				return localOutput;
		}
	}
	
	@Override
	public void stopTask() {
		asyncOutputWorkers.shutdownNow();
	}


}
