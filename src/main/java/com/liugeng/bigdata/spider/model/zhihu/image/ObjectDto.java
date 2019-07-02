package com.liugeng.bigdata.spider.model.zhihu.image;

import java.util.Date;

import lombok.Data;

@Data
public class ObjectDto {
	
	private String id;
	private String type;
	private String excerpt;
	private String url;
	private int voteup_count;
	private int comment_count;
	private Date created_time;
	private Date updated_time;
	private String content;
	private ThumbnailInfoDto thumbnail_info;
	private RelationshipDto relationship;
	private QuestionDto question;
	private AuthorDto author;
	private Object flag;
	private String attached_info_bytes;
}
