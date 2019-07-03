package com.liugeng.bigdata.spider.task;

import static com.liugeng.bigdata.spider.common.Constants.*;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.liugeng.bigdata.spider.parser.ZhihuSearchImageParser;
import com.liugeng.bigdata.spider.utils.ZhihuSpiderUtils;
import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.rundata.RunData;
import com.xuxueli.crawler.rundata.strategy.LocalRunData;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 16:03
 */
@JobHandler("zhihuSearchImageTask")
@Getter
@Setter
public class ZhihuSearchImageTask extends IJobHandler {
	
	private String fileBasePath;
	private String searchWord;
	private int workers;
	private ForkJoinPool asyncOutputWorkers;
	
	@Override
	public ReturnT<String> execute(String params) throws Exception {
		XxlJobLogger.log("开始知乎爬图");
		Map<String, String> searchParams = ZhihuSpiderUtils.buildParams(searchWord, 20, 0);
		RunData runData = new LocalRunData();
		runData.addUrl(ZHIHU_SEARCH_API_BASE);
		ZhihuSearchImageParser parser = new ZhihuSearchImageParser(runData, fileBasePath, asyncOutputWorkers);
		@Cleanup("stop")
		XxlCrawler crawler = new XxlCrawler.Builder()
			.setRunData(runData)
			.setPageParser(parser)
			.setParamMap(searchParams)
			.build();
		crawler.start(true);
		parser.syncAwait(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
		return ReturnT.SUCCESS;
	}
	
	@PostConstruct
	public void setAsyncWorkers() {
		asyncOutputWorkers = (ForkJoinPool)Executors.newWorkStealingPool(workers);
	}
	
	@PreDestroy
	public void stopTask() {
		asyncOutputWorkers.shutdownNow();
	}
}
