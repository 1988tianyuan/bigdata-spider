package com.liugeng.bigdata.spider.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/8/26 16:05
 */
@Component
public class SpiderProxyUtil {

	private final List<Proxy> proxyList = new ArrayList<>(100);
	
	public Proxy getRandomProxy() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int i = random.nextInt(100);
		return proxyList.get(i);
	}
	
	
}
