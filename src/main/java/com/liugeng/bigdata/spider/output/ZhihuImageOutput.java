package com.liugeng.bigdata.spider.output;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Preconditions;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ObjectDto;
import com.liugeng.bigdata.spider.utils.RegexUtils;
import com.xuxueli.crawler.util.FileUtil;
import com.xuxueli.crawler.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 10:41
 */
@Slf4j
public class ZhihuImageOutput implements DataOutput<List<DataDto>> {
	
	private String fileBasePath;
	private ForkJoinPool asyncOutputWorkers;
	
	public ZhihuImageOutput(String fileBasePath, ForkJoinPool asyncOutputWorkers) {
		this.fileBasePath = fileBasePath;
		this.asyncOutputWorkers = asyncOutputWorkers;
	}
	
	@Override
	public void output(List<DataDto> dataList) {
		Preconditions.checkNotNull(fileBasePath, "fileBasePath should not be null!");
		for (DataDto data : dataList) {
			ObjectDto objectDto = data.getObject();
			if (objectDto == null || StringUtils.isBlank(objectDto.getContent())) {
				continue;
			}
			String content = objectDto.getContent();
			Document html = Jsoup.parse(content);
			Set<String> imgs = JsoupUtil.findImages(html).stream().filter(RegexUtils::isHttpUrl).collect(Collectors.toSet());
			log.info("本次爬图任务共发现{}个图片链接", imgs.size());
			final AtomicInteger downloadNum = new AtomicInteger(0);
			for (String imgUrl : imgs) {
				String imgName = StringUtils.substringAfterLast(imgUrl, "/");
				CompletableFuture<Boolean> cf = CompletableFuture.supplyAsync(
					() -> FileUtil.downFile(imgUrl, 10000, fileBasePath, imgName), asyncOutputWorkers
				);
				cf.whenCompleteAsync((result, error) -> {
					int current = downloadNum.incrementAndGet();
					if (!result && error != null) {
						log.error("下载第{}张图片:{}的时候发生错误", current, imgUrl, error);
					} else {
						log.info("成功下载第{}张图片{}", current, imgUrl);
					}
				});
			}
		}
	}
	
	@Override
	public void await(long time, TimeUnit timeUnit) {
		boolean isQuiescent = asyncOutputWorkers.awaitQuiescence(time, timeUnit);
		log.info(isQuiescent ? "ZhihuImageOutput is finished" : "ZhihuImageOutput is still running!");
	}
}
