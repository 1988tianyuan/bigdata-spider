package com.liugeng.bigdata.spider.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 14:13
 */
@Component
@Slf4j
public class KafkaSender {
	
	@Autowired
	private KafkaTemplate<String, Object> zhihuKafkaTemplate;
	
	public void sendDataToKafka(String topic, Object dto) {
		ListenableFuture<SendResult<String, Object>> future = zhihuKafkaTemplate.send(topic, dto);
		future.addCallback(new KafkaCallback());
		zhihuKafkaTemplate.flush();
	}
	
	private static class KafkaCallback implements ListenableFutureCallback<SendResult<String, Object>> {
		
		@Override
		public void onFailure(Throwable throwable) {
			log.error("向kafka发送数据时出现错误", throwable);
		}
		
		@Override
		public void onSuccess(SendResult<String, Object> result) {
			log.info("成功向kafka发送数据", result);
		}
	}
}
