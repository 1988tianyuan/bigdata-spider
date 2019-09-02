package com.liugeng.bigdata.spider.task;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;

import com.liugeng.bigdata.spider.exception.SpiderTaskException;
import com.liugeng.bigdata.spider.output.DataOutput;
import com.liugeng.bigdata.spider.parser.ZhihuSearchParser;
import com.liugeng.bigdata.spider.utils.SpringBeanUtils;
import com.xuxueli.crawler.rundata.RunData;
import com.xxl.job.core.handler.IJobHandler;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 15:05
 */
public abstract class ZhihuSpiderTask extends IJobHandler {
	
	protected Map<String, DataOutput> outputMap;
	protected ForkJoinPool asyncOutputWorkers;
	@Value("${spider.zhihu.workers}")
	protected int workers;
	
	public abstract void stopTask();
	
	protected ZhihuSearchParser getJsonPageParser(TaskType taskType, RunData runData) {
		ZhihuSearchParser pageParser;
		try {
			if (taskType.equals(TaskType.IMAGE)) {
				pageParser = SpringBeanUtils.getBean("zhihuSearchImageParser", ZhihuSearchParser.class);
			} else {
				pageParser = SpringBeanUtils.getBean("zhihuSearchParser", ZhihuSearchParser.class);
			}
			pageParser.setRunData(runData);
		} catch (BeansException e) {
			throw new SpiderTaskException("Can't find pageParser of this type: " + taskType);
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
