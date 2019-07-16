package com.liugeng.bigdata.spider.parser;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.liugeng.bigdata.spider.output.FileOutput;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ZhihuApiResponse;
import com.liugeng.bigdata.spider.output.ZhihuDataOutPut;
import com.liugeng.bigdata.spider.utils.RegexUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component("zhihuSearchImageParser")
@Scope("prototype")
public class ZhihuSearchImageParser extends SpiderJsonParser {
	
	private FileOutput dataOutput;
	
	@Override
	public void parse(String url, String source) {
		ZhihuApiResponse response = JSON.parseObject(source, ZhihuApiResponse.class);
		boolean isEnd = response.getPaging().is_end();
		String nextUrl = response.getPaging().getNext();
		if (!isEnd && RegexUtils.isHttpUrl(nextUrl)) {
			addUrl(nextUrl);
		}
		List<DataDto> dataList = response.getData();
		if (CollectionUtils.isNotEmpty(dataList)) {
			dataOutput.output(dataList);
		}
	}
	
	public void syncAwait(long time, TimeUnit timeUnit) {
		dataOutput.await(time, timeUnit);
	}
}
