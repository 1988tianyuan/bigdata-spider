package com.liugeng.bigdata.spider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liugeng.bigdata.spider.task.ZhihuSearchImageTask;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 14:26
 */
@Configuration
@Slf4j
public class SpiderTaskConfig {

//	@Bean(initMethod = "start", destroyMethod = "destroy")
	@ConfigurationProperties(prefix = "xxl.job")
	public XxlJobSpringExecutor xxlJobSpringExecutor(InetUtils inetUtils) throws Exception {
		XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
		String ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
		xxlJobSpringExecutor.setIp(ip);
		xxlJobSpringExecutor.start();
		return xxlJobSpringExecutor;
	}
	
	@Bean(initMethod = "initTask", destroyMethod = "stopTask")
	@ConfigurationProperties(prefix = "spider.zhihu")
	public ZhihuSearchImageTask zhihuSearchImageTask() {
		return new ZhihuSearchImageTask();
	}
}
