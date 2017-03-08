package com.github.emailtohl.building.site.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 可解析网页
 * @author HeLei
 * @date 2017.03.08
 */
@Component
public class Crawler {
	private static final Logger logger = LogManager.getLogger();
	@Value("${proxyHost}")
	private String proxyHost;
	@Value("${proxyPort}")
	private String proxyPort;
	
	public Connection getConnection(String url) {
		Connection conn = Jsoup.connect(url)
		.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		.header("Accept-Encoding", "gzip, deflate, sdch")
		.header("Accept-Language", "zh-CN,zh;q=0.8")
		.header("Host", "epub.cqvip.com")
		.header("Proxy-Connection", "keep-alive")
		.header("Referer", "http://epub.cqvip.com/manage/_main_left.aspx")
		.header("Upgrade-Insecure-Requests", "1")
		.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
//		if (StringUtils.hasText(proxyHost) && StringUtils.hasText(proxyPort)) {
//			conn.proxy(proxyHost, Integer.valueOf(proxyPort));
//		}
		return conn;
	}
	
	@Scheduled(fixedDelay = 50000)
	public void fetch() throws IOException {
		Connection conn = getConnection("http://localhost:8080/building/article");
//		conn.cookie("cookie_admin_username", "zt")
//		.cookie("cookie_admin_password", "4da64b5779c9d82140c450b33124ccc3");
		Document doc = conn.get();
		if (logger.isDebugEnabled()) {
			logger.debug(doc);
		}
		doc.getElementsByTag("a").forEach(a -> {
			String href = a.attr("href");
			if (StringUtils.hasText(href) && href.startsWith("detail?id=")) {
				Connection subConn = getConnection("http://localhost:8080/building/" + href);
				try {
					Document detailDoc = subConn.get();
					String body = detailDoc.getElementById("article-body").text();
					logger.debug(body);
				} catch (IOException e) {
					logger.catching(e);
				}
			}
		});
	}
}
