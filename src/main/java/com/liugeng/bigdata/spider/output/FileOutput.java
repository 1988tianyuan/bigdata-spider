package com.liugeng.bigdata.spider.output;


/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version v1  Date: 2019/7/16
 */
public abstract class FileOutput<T> implements DataOutput<T> {
	
	protected String uri;
	
	public FileOutput() {
	}
	
	public FileOutput(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
}
