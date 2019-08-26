package com.liugeng.bigdata.spider.task;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;

import com.liugeng.bigdata.spider.exception.SpiderTaskException;
import com.liugeng.bigdata.spider.output.DataOutput;
import com.liugeng.bigdata.spider.parser.SpiderJsonParser;
import com.liugeng.bigdata.spider.utils.SpringBeanUtils;
import com.xuxueli.crawler.rundata.RunData;
import com.xxl.job.core.handler.IJobHandler;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 15:05
 */
public abstract class SpiderTask extends IJobHandler {
	
	protected Map<String, DataOutput> outputMap;
	protected ForkJoinPool asyncOutputWorkers;
	@Value("${spider.zhihu.workers}")
	protected int workers;
	
	public abstract void stopTask();
	
	protected SpiderJsonParser getJsonPageParser(String parserName, RunData runData) {
		SpiderJsonParser pageParser;
		try {
			pageParser = SpringBeanUtils.getBean(parserName, SpiderJsonParser.class);
			pageParser.setRunData(runData);
		} catch (BeansException e) {
			throw new SpiderTaskException("Can't find pageParser:" + parserName);
		}
		return pageParser;
	}
	
	protected void initTask() {
		asyncOutputWorkers = (ForkJoinPool)Executors.newWorkStealingPool(workers);
		outputMap = initOutputList();
	}
	
	public void setAsyncOutputWorkers(ForkJoinPool asyncOutputWorkers) {
		this.asyncOutputWorkers = asyncOutputWorkers;
	}
	
	protected abstract Map<String, DataOutput> initOutputList();
}
