package com.liugeng.bigdata.spider.task;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import com.liugeng.bigdata.spider.exception.SpiderTaskException;
import com.liugeng.bigdata.spider.parser.SpiderJsonParser;
import com.liugeng.bigdata.spider.utils.SpringBeanUtils;
import com.xuxueli.crawler.rundata.RunData;
import com.xxl.job.core.handler.IJobHandler;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 15:05
 */
public abstract class SpiderTask extends IJobHandler {
	
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
}
