package com.liugeng.bigdata.spider.output.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.liugeng.bigdata.spider.model.zhihu.ZhihuToEsDto;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ObjectDto;
import com.liugeng.bigdata.spider.output.DataOutput;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/7/17 17:44
 */
@Component("zhihuDataEsOutput")
@Setter
@Getter
@Slf4j
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
			String content = objectDto.getContent();
			Date createDate = objectDto.getCreated_time();
			String type = objectDto.getType();
			long id = Long.parseLong(objectDto.getId());
			String title = data.getHighlight().getTitle();
			String apiUrl = objectDto.getUrl();
			String author = objectDto.getAuthor().getName();
			ZhihuToEsDto dto = ZhihuToEsDto.builder()
				.apiUrl(apiUrl).content(content)
				.date(createDate).type(type)
				.zhihuId(id).title(title)
				.author(author)
				.build();
			UpdateRequest updateRequest = new UpdateRequest("zhihu_search_" + type, String.valueOf(id))
				.docAsUpsert(true).doc(JSON.toJSONString(dto, false), XContentType.JSON);
			client.updateAsync(updateRequest, RequestOptions.DEFAULT, new ZhihuEsCallback());
		}
	}
	
	@Override
	public void await(long time, TimeUnit timeUnit) {
	
	}
	
	@Override
	public void setAsyncOutputWorkers(ForkJoinPool asyncOutputWorkers) {
	
	}
	
	private static class ZhihuEsCallback implements ActionListener<UpdateResponse> {
		@Override
		public void onResponse(UpdateResponse updateResponse) {
			log.info("update successfully, status: {}", updateResponse.status().getStatus());
		}
		@Override
		public void onFailure(Exception e) {
			log.warn("Failed to update", e);
		}
	}
}
