package com.liugeng.bigdata.spider.parser;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component("zhihuSearchImageParser")
@Scope("prototype")
public class ZhihuSearchImageParser extends ZhihuSearchParser implements AsyncParser {
	
	@Override
	public void syncAwait(long time, TimeUnit timeUnit) {
		dataOutput.await(time, timeUnit);
	}
}
