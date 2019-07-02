package com.liugeng.bigdata.spider.utils;

import java.util.regex.Pattern;

public class RegexUtils {
	
	private static final Pattern HTTP_URL_PATTERN
		= Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	
	public static boolean isHttpUrl(String str) {
		if (str != null) {
			return HTTP_URL_PATTERN.matcher(str).matches();
		}
		return false;
	}
}
