package com.github.emailtohl.building.common.lucene;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.github.emailtohl.building.common.utils.UpDownloader;
/**
 * 文件搜索器的测试
 * @author HeLei
 * @date 2017.02.04
 */
public class FileSearchTest {
	private static final Logger logger = LogManager.getLogger();
	private static final String PATH = "src/test/java";
	private static final String SEARCH_QUERY = "@Test";
	Random r = new Random();
	File tempFile;
	RAMDirectory directory;
	FileSearch fs;

	@Before
	public void setUp() throws Exception {
		File thisFile = new File(PATH + File.separator + UpDownloader.convertPackageNameToFilePath(getClass().getName()) + ".java");
		tempFile = new File(System.getProperty("java.io.tmpdir"), "testFileSearch.txt");
		FileUtils.copyFile(thisFile.getAbsoluteFile(), tempFile);
		
		// 创建一个内存目录
		directory = new RAMDirectory();
		fs = new FileSearch(directory);
		int numIndexed = fs.index(tempFile);
		logger.debug(numIndexed);
	}
	
	@After
	public void tearDown() throws Exception {
		fs.close();
		tempFile.delete();
	}
	
	@Test
	public void test() throws IOException, InterruptedException {
		Set<String> result = fs.queryForFilePath(SEARCH_QUERY);
		result.forEach(s -> logger.debug(s));
		assertFalse(result.isEmpty());
		
		Order[] orders = {new Order(FileSearch.FILE_NAME), new Order(FileSearch.FILE_CONTENT)};
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(1, 5, sort);
		Page<Document> page = fs.query(SEARCH_QUERY, pageable);
		logger.debug(page.getNumber());
		logger.debug(page.getSize());
		logger.debug(page.getTotalElements());
		for (Document d : page.getContent()) {
			logger.debug(d);
		}
		
		// 测试并发
		short count = 100;
		CountDownLatch latch = new CountDownLatch(count);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < count; i++) {
			exec.submit(() -> {
				try {
					FileUtils.writeStringToFile(tempFile, r.nextInt(100) + " ", StandardCharsets.UTF_8, true);
					fs.updateIndex(tempFile);
					latch.countDown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		for (int i = 0; i < count; i++) {
			exec.submit(() -> {
				fs.queryForFilePath(getClass().getSimpleName());
			});
		}
		latch.await();
		result = fs.queryForFilePath(getClass().getSimpleName());
		logger.debug(result);
		assertFalse(result.isEmpty());
		fs.deleteIndex(tempFile);
	}
	
}
