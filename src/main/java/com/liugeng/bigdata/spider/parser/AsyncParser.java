package com.liugeng.bigdata.spider.parser;

import java.util.concurrent.TimeUnit;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/8/27 16:14
 */
public interface AsyncParser {
	
	void syncAwait(long time, TimeUnit timeUnit);
}
