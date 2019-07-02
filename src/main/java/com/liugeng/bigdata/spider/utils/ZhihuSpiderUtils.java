package com.liugeng.bigdata.spider.utils;

import java.util.HashMap;
import java.util.Map;

public class ZhihuSpiderUtils {
	
	public static Map<String, String> buildParams(String searchWord, int limit, int offset) {
		Map<String, String> searchParams = new HashMap<>();
		searchParams.put("advert_count", "0");
		searchParams.put("correction", "1");
		searchParams.put("lc_idx", "0");
		searchParams.put("limit", String.valueOf(limit));
		searchParams.put("offset", String.valueOf(offset));
		searchParams.put("q", searchWord);
		searchParams.put("search_hash_id", "89482156a631e3b7aa2a1fd42749516b");
		searchParams.put("show_all_topics", "0");
		searchParams.put("t", "general");
		searchParams.put("vertical_info", "0,1,1,1,0,0,0,0,0,1");
		return searchParams;
	}
}
