package com.liugeng.bigdata.spider.model.zhihu.image;

import java.util.List;

import lombok.Data;

@Data
public class ThumbnailInfoDto {
	
	private int count;
	private String type;
	private List<ThumbnailsDto> thumbnails;
}
