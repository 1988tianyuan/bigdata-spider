package com.liugeng.bigdata.spider.web;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 14:05
 */
@RestController
@RequestMapping("/zhihu")
public class ZhihuSpiderController {
	
	@Autowired
	private RestHighLevelClient client;
	
	@GetMapping
	public Map<String, Object> test() throws IOException {
		GetRequest getRequest = new GetRequest("test_liugeng_1", "doc", "1");
		GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
		return response.getSource();
	}
}
