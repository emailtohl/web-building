package com.github.emailtohl.building.common.lucene;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import com.github.emailtohl.building.common.lucene.FileSearch;
import com.github.emailtohl.building.common.utils.UpDownloader;
/**
 * 文件搜索器的测试
 * @author HeLei
 * @date 2017.02.04
 */
public class FileSearchTest {
	private static final Logger logger = LogManager.getLogger();
	private static final String PATH = "src/test/java";
	private static final String SEARCH_QUERY = "public";
	RAMDirectory directory;
	FileSearch fs;

	@Before
	public void setUp() throws Exception {
		// 创建一个内存目录
		directory = new RAMDirectory();
		fs = new FileSearch(directory);
		int numIndexed = fs.index(new File(PATH));
		logger.debug(numIndexed);
	}
	
	@After
	public void tearDown() throws Exception {
		fs.close();
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
		short count = 10;
		File thisFile = new File(PATH + File.separator + UpDownloader.convertPackageNameToFilePath(getClass().getName()));
		CountDownLatch latch = new CountDownLatch(count);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < count; i++) {
			exec.submit(() -> {
				try {
					fs.updateIndex(thisFile);
					latch.countDown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		latch.await();
		result = fs.queryForFilePath(getClass().getSimpleName());
		logger.debug(result);
		assertFalse(result.isEmpty());
		fs.deleteIndex(thisFile);
	}
	
}
