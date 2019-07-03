package com.liugeng.bigdata.spider.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liugeng.bigdata.spider.task.ZhihuSearchImageTask;
import com.liugeng.bigdata.spider.task.ZhihuSpiderTask;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 14:26
 */
@Configuration
@Slf4j
public class SpiderTaskConfig {
	
	@Value("${xxl.job.port}")
	private int port;
	@Value("${xxl.job.adminAddresses}")
	private String adminAddress;
	@Value("${xxl.job.appName}")
	private String appName;
	
	@Bean
	@ConfigurationProperties(prefix = "xxl.job")
	public XxlJobSpringExecutor xxlJobSpringExecutor() {
		XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
		xxlJobSpringExecutor.setPort(port);
		xxlJobSpringExecutor.setAdminAddresses(adminAddress);
		xxlJobSpringExecutor.setAppName(appName);
		log.info("xxl job init:{}", xxlJobSpringExecutor);
		return xxlJobSpringExecutor;
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spider.zhihu")
	public ZhihuSearchImageTask zhihuSearchImageTask() {
		return new ZhihuSearchImageTask();
	}
}
