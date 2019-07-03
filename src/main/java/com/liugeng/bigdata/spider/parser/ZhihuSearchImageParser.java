package com.liugeng.bigdata.spider.parser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ZhihuApiResponse;
import com.liugeng.bigdata.spider.output.DataOutput;
import com.liugeng.bigdata.spider.output.ZhihuImageOutput;
import com.liugeng.bigdata.spider.utils.RegexUtils;
import com.xuxueli.crawler.parser.strategy.NonPageParser;
import com.xuxueli.crawler.rundata.RunData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZhihuSearchImageParser extends NonPageParser {
	
	private final RunData runData;
	private DataOutput<List<DataDto>> dataOutput;
	
	public ZhihuSearchImageParser(RunData runData, String fileBasePath, ForkJoinPool asyncOutputWorkers) {
		Preconditions.checkNotNull(fileBasePath, "fileBasePath should not be null!");
		this.runData = runData;
		this.dataOutput = new ZhihuImageOutput(fileBasePath, asyncOutputWorkers);
	}
	
	@Override
	public void parse(String url, String source) {
		ZhihuApiResponse response = JSON.parseObject(source, ZhihuApiResponse.class);
		boolean isEnd = response.getPaging().is_end();
		String nextUrl = response.getPaging().getNext();
		if (!isEnd && RegexUtils.isHttpUrl(nextUrl)) {
			runData.addUrl(nextUrl);
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
