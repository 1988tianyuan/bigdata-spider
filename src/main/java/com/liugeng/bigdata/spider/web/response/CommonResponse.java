package com.liugeng.bigdata.spider.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
	private T content;

	private Integer status;

	public static <T> CommonResponse<T> of(T content) {
		return CommonResponse.<T>builder().content(content).build();
	}

	public Integer getStatus() {
		if (status == null) {
			return 0;
		}
		return status;
	}
}
