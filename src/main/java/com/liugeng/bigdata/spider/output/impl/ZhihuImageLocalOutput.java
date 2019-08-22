package com.liugeng.bigdata.spider.output.impl;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.liugeng.bigdata.spider.exception.SpiderTaskException;
import com.liugeng.bigdata.spider.model.zhihu.image.DataDto;
import com.liugeng.bigdata.spider.model.zhihu.image.ObjectDto;
import com.liugeng.bigdata.spider.output.FileOutput;
import com.liugeng.bigdata.spider.utils.RegexUtils;
import com.xuxueli.crawler.util.FileUtil;
import com.xuxueli.crawler.util.JsoupUtil;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 天渊 1988tianyuan@gmail.com
 * @version 创建时间：2019/7/3 10:41
 */
@Slf4j
@Component("zhihuImageLocalOutput")
public class ZhihuImageLocalOutput extends FileOutput<List<DataDto>> {
	
	private ForkJoinPool asyncOutputWorkers;
	
	@Autowired
	public ZhihuImageLocalOutput(@Value("${spider.zhihu.fileBasePath}")String fileBasePath) {
		this.uri = fileBasePath;
	}
	
	@Override
	public void output(List<DataDto> dataList) {
		Preconditions.checkNotNull(uri, "fileBasePath should not be null!");
		checkDirExists(uri);
		XxlJobLogger.log("爬图保存地址为：" + uri);
		for (DataDto data : dataList) {
			ObjectDto objectDto = data.getObject();
			if (objectDto == null || StringUtils.isBlank(objectDto.getContent())) {
				continue;
			}
			String content = objectDto.getContent();
			Document html = Jsoup.parse(content);
			Set<String> imgs = JsoupUtil.findImages(html).stream().filter(RegexUtils::isHttpUrl).collect(Collectors.toSet());
			log.info("本次爬图任务共发现{}个图片链接", imgs.size());
			for (String imgUrl : imgs) {
				String imgName = StringUtils.substringAfterLast(imgUrl, "/");
				CompletableFuture<Boolean> cf = CompletableFuture.supplyAsync(
					() -> FileUtil.downFile(imgUrl, 10000, uri, imgName), asyncOutputWorkers
				);
				cf.whenCompleteAsync((result, error) -> {
					if (!result || error != null) {
						log.error("下载第图片:{}的时候发生错误", imgUrl, error);
					} else {
						log.info("成功下载图片{}", imgUrl);
					}
				});
			}
		}
	}
	
	private void checkDirExists(String fileBasePath) {
		File file = new File(fileBasePath);
		if (!file.exists()) {
			try {
				boolean created = file.mkdirs();
			} catch (Exception e) {
				throw new SpiderTaskException("创建文件夹时发生错误", e);
			}
		}
	}
	
	@Override
	public void await(long time, TimeUnit timeUnit) {
		boolean isQuiescent = asyncOutputWorkers.awaitQuiescence(time, timeUnit);
		log.info(isQuiescent ? "ZhihuImageLocalOutput is finished" : "ZhihuImageLocalOutput is still running!");
	}
	
	@Override
	public void setAsyncOutputWorkers(ForkJoinPool asyncOutputWorkers) {
		this.asyncOutputWorkers = asyncOutputWorkers;
	}
}
