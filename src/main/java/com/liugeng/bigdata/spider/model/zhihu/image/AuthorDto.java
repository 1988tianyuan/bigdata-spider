package com.liugeng.bigdata.spider.model.zhihu.image;

import java.util.List;

import lombok.Data;

@Data
public class AuthorDto {
	
	private String id;
	private String url_token;
	private String name;
	private String headline;
	private int gender;
	private boolean is_followed;
	private boolean is_following;
	private String user_type;
	private String url;
	private String type;
	private String avatar_url;
	private List<?> badge;
}
