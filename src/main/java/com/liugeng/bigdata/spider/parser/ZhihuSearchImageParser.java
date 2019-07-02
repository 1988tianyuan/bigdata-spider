package com.liugeng.bigdata.spider.parser;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ObjectDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ZhihuApiResponse;
import com.liugeng.bigdata.spider.utils.RegexUtils;
import com.xuxueli.crawler.parser.strategy.NonPageParser;
import com.xuxueli.crawler.rundata.RunData;
import com.xuxueli.crawler.util.FileUtil;
import com.xuxueli.crawler.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZhihuSearchImageParser extends NonPageParser {
	
	private final RunData runData;
	private final ExecutorService threadPool;
	private final String fileBasePath;
	
	public ZhihuSearchImageParser(RunData runData, ExecutorService threadPool, String fileBasePath) {
		Preconditions.checkNotNull(fileBasePath, "fileBasePath should not be null!");
		this.runData = runData;
		this.threadPool = threadPool;
		this.fileBasePath = fileBasePath;
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
			for (DataDto data : dataList) {
				ObjectDto objectDto = data.getObject();
				if (objectDto == null || StringUtils.isBlank(objectDto.getContent())) {
					continue;
				}
				String content = objectDto.getContent();
				Document html = Jsoup.parse(content);
				Set<String> imgs = JsoupUtil.findImages(html).stream().filter(RegexUtils::isHttpUrl).collect(Collectors.toSet());
				for (String imgUrl : imgs) {
					String imgName = StringUtils.substringAfterLast(imgUrl, "/");
					CompletableFuture<Boolean> cf = CompletableFuture.supplyAsync(
						() -> FileUtil.downFile(imgUrl, 10000, fileBasePath, imgName), threadPool
					);
					cf.whenCompleteAsync((result, error) -> {
						if (!result && error != null) {
							log.error("下载{}的时候发生错误", imgUrl);
						} else {
							log.info("成功下载图片{}", imgUrl);
						}
					});
				}
			}
		}
	}
}
