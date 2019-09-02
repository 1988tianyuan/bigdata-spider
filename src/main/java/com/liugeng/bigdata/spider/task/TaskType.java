package com.liugeng.bigdata.spider.task;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/9/2 10:14
 */
public enum TaskType {
	
	DATA("data"), IMAGE("image");
	
	private String type;
	
	TaskType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
