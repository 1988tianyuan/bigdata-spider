package com.liugeng.bigdata.spider.output;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.liugeng.bigdata.spider.kafka.KafkaSender;
import com.liugeng.bigdata.spider.model.zhihu.ZhihuToKafkaDto;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.HighlightDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ObjectDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 14:41
 */
@Component("zhihuKafkaOutput")
@Scope("prototype")
@Setter
@Getter
public class ZhihuKafkaOutput implements ZhihuDataOutPut {
	
	@Value("${spider.zhihu.kafkaTopic}")
	private String topic;
	@Autowired
	private KafkaSender sender;
	
	private final List<ListenableFuture<SendResult<String, Object>>> futures
		= new CopyOnWriteArrayList<>();
	
	@Override
	public void output(List<DataDto> dataDtoList) {
		for (DataDto data : dataDtoList) {
			ObjectDto objectDto = data.getObject();
			HighlightDto highlight = data.getHighlight();
			if (objectDto != null && highlight != null) {
				ZhihuToKafkaDto kafkaDto = ZhihuToKafkaDto.builder()
					.apiUrl(objectDto.getUrl())
					.content(objectDto.getContent())
					.title(highlight.getTitle())
					.type(objectDto.getType())
					.zhihuId(NumberUtils.toLong(objectDto.getId(), 0))
					.build();
				sender.sendDataToKafka(topic, kafkaDto);
			}
		}
	}
	
	@Override
	public void await(long time, TimeUnit timeUnit) {
		try {
			for (ListenableFuture<SendResult<String, Object>> future : futures) {
				future.get(time, timeUnit);
			}
		} catch (Exception ignore) {
			// just ignore
		}
	}
}
