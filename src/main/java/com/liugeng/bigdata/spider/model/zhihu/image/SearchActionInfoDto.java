package com.liugeng.bigdata.spider.model.zhihu.image;

import lombok.Data;

@Data
public class SearchActionInfoDto {

	private String attached_info_bytes;
	private int lc_idx;
	private String search_hash_id;
	private boolean isfeed;
}
