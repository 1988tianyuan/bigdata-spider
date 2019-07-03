package com.liugeng.bigdata.spider.task;

import static com.liugeng.bigdata.spider.common.Constants.*;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ForkJoinPoolFactoryBean;
import org.springframework.stereotype.Component;

import com.liugeng.bigdata.spider.parser.ZhihuSearchImageParser;
import com.liugeng.bigdata.spider.utils.ZhihuSpiderUtils;
import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.rundata.RunData;
import com.xuxueli.crawler.rundata.strategy.LocalRunData;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 11:32
 */
@Slf4j
@Setter
@Getter
public class ZhihuSpiderTask {
	
	private String fileBasePath;
	private String searchWord;
	private int workers;
	
	private ForkJoinPool asyncOutputWorkers;

	public void imageSearchTask() {
		log.info("开始知乎爬图");
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
