package com.liugeng.bigdata.spider.output.impl;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.output.DataOutput;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/7/17 17:44
 */
@Component("ZhihuDataEsOutput")
@Setter
@Getter
public class ZhihuDataEsOutput implements DataOutput<List<DataDto>> {
	
	@Autowired
	private RestHighLevelClient client;
	
	@Override
	public void output(List<DataDto> dataList) {
	
	}
	
	@Override
	public void await(long time, TimeUnit timeUnit) {
	
	}
	
	@Override
	public void setAsyncOutputWorkers(ForkJoinPool asyncOutputWorkers) {
	
	}
}
