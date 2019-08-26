package com.liugeng.bigdata.spider.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/search")
	public List<Map<String, Object>> searchByKeyWord(@RequestParam String keyWord) throws IOException {
		SearchRequest searchRequest = new SearchRequest("test_liugeng_1");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("name", keyWord));
		searchSourceBuilder.highlighter(createHighlightBuilder("name"));
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] searchHits = searchResponse.getHits().getHits();
		List<Map<String, Object>> results = new LinkedList<>();
		if (searchHits != null && searchHits.length > 0) {
			for (SearchHit searchHit : searchHits) {
				Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
				HighlightField nameField = highlightFields.get("name");
				Text text = nameField.fragments()[0];
				Map<String, Object> docResult = new HashMap<>(searchHit.getSourceAsMap());
				docResult.put("hight_name", text.string());
				results.add(docResult);
			}
		}
		return results;
	}
	
	private HighlightBuilder createHighlightBuilder(String fieldName) {
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		HighlightBuilder.Field field = new HighlightBuilder.Field(fieldName);
		field.highlighterType("unified");
		highlightBuilder.field(field);
		highlightBuilder.preTags("<h4>");
		highlightBuilder.postTags("</h4>");
		return highlightBuilder;
	}
	
	@GetMapping("/{id}")
	public Map<String, Object> getById(@PathVariable String id) throws IOException {
		GetRequest getRequest = new GetRequest("test_liugeng_1", id);
		GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
		return response.getSource();
	}
	
	@PostMapping
	public String insertNew(@RequestBody Map<String, Object> source) throws IOException {
		IndexRequest indexRequest = new IndexRequest("test_liugeng_1")
			.opType(DocWriteRequest.OpType.INDEX).source(source);
		IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
		return "The response of insert status: " + response.status().getStatus();
	}
	
	@PatchMapping("/{id}")
	public String update(@RequestBody Map<String, Object> source, @PathVariable String id) throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("test_liugeng_1", id)
			.docAsUpsert(true).doc(source);
		UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
		return "The response of update status: " + response.status().getStatus();
	}
	
	@PutMapping("/{id}")
	public String putExists(@RequestBody Map<String, Object> source, @PathVariable String id) throws IOException {
		IndexRequest indexRequest = new IndexRequest("test_liugeng_1")
			.id(id).opType(DocWriteRequest.OpType.INDEX).source(source);
		IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
		return "The response of put status: " + response.status().getStatus();
	}
	
	@DeleteMapping("/{id}")
	public String deleteById(@PathVariable String id) throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("test_liugeng_1", id);
		DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
		return "The response of delete status: " + response.status().getStatus();
	}
}
