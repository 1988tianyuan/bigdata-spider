package com.liugeng.bigdata.spider.output;

import java.util.concurrent.TimeUnit;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 10:41
 */
public interface DataOutput<T> {

	void output(T data);
	
	void await(long time, TimeUnit timeUnit);
}
