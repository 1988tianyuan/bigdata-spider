package com.liugeng.bigdata.spider.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/7/23 16:35
 */
@Configuration
public class EsConfig {
	
	@Value("${elasticsearch.host}")
	private String host;
	@Value("${elasticsearch.port}")
	private int port;
	
	
	@Bean(destroyMethod = "close")
	public RestHighLevelClient client() {
		return new RestHighLevelClient(
			RestClient.builder(new HttpHost(host, port, "http")));
	}
}
