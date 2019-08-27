package com.liugeng.bigdata.spider.output.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ObjectDto;
import com.liugeng.bigdata.spider.output.DataOutput;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/7/17 17:44
 */
@Component("zhihuDataEsOutput")
@Setter
@Getter
public class ZhihuDataEsOutput implements DataOutput<List<DataDto>> {
	
	@Autowired
	private RestHighLevelClient client;
	
	@Override
	public void output(List<DataDto> dataList) {
		for (DataDto data : dataList) {
			ObjectDto objectDto = data.getObject();
			if (objectDto == null || StringUtils.isBlank(objectDto.getContent())) {
				continue;
			}
			String content = objectDto.getExcerpt();
			String id = objectDto.getId();
			Date createDate = objectDto.getCreated_time();
			String type = objectDto.getType();
			System.out.println(String.format("content是%s, id是%s, createDate是%s, type是%s", content, id, createDate.toString(), type));
		}
	}
	
	@Override
	public void await(long time, TimeUnit timeUnit) {
	
	}
	
	@Override
	public void setAsyncOutputWorkers(ForkJoinPool asyncOutputWorkers) {
	
	}
}
