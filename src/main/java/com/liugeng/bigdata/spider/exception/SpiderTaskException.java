package com.liugeng.bigdata.spider.exception;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 13:42
 */
public class SpiderTaskException extends RuntimeException {
	
	public SpiderTaskException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SpiderTaskException(String message) {
		super(message);
	}
}
