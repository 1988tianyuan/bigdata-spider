package com.liugeng.bigdata.spider.model.zhihu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/4 13:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZhihuToKafkaDto {

	private String title;
	private String type;
	private long zhihuId;
	private String content;
	private String apiUrl;
}
