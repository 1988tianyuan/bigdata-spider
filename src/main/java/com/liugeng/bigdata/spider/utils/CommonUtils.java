package com.liugeng.bigdata.spider.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 11:05
 */
public class CommonUtils {
	
	
	public static Map<String, String> parseStringParams(String params) {
		if (params == null) {
			return null;
		}
		Map<String, String> paramMap = new HashMap<>();
		StringTokenizer tokenizer = new StringTokenizer(params);
		while (tokenizer.hasMoreElements()) {
			String param = tokenizer.nextToken();
			if (param.contains("--") && param.contains("=")) {
				String key = StringUtils.substringBetween(param, "--", "=");
				String value = StringUtils.substringAfter(param, "=");
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}
}
