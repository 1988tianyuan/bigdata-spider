package com.liugeng.bigdata.spider.output;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/9/2 10:14
 */
public enum OutputType {
	
	ES("es"), LOCAL("local");
	
	private String type;
	
	OutputType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
