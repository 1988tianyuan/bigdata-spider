package com.liugeng.bigdata.spider.parser;

import com.google.common.base.Preconditions;
import com.xuxueli.crawler.parser.strategy.NonPageParser;
import com.xuxueli.crawler.rundata.RunData;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 15:25
 */
public abstract class SpiderJsonParser extends NonPageParser {
	
	protected RunData runData;
	
	protected void addUrl(String url) {
		Preconditions.checkNotNull(runData, "RunData should not be null!");
		runData.addUrl(url);
	}
	
	public RunData getRunData() {
		return runData;
	}
	
	public void setRunData(RunData runData) {
		this.runData = runData;
	}
}
