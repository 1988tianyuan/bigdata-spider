package com.liugeng.bigdata.spider.model.zhihu;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/9/2 13:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZhihuToEsDto {
	
	private String author;
	private String title;
	private String type;
	private long zhihuId;
	private String content;
	private String apiUrl;
	private Date date;
}
