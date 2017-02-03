package com.github.emailtohl.building.common.lucene;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Set;

import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.building.common.lucene.FileSearch;

public class FileSearchTest {
	RAMDirectory directory;
	FileSearch fs;

	@Before
	public void setUp() throws Exception {
		// 创建一个内存目录
		directory = new RAMDirectory();
		fs = new FileSearch(directory);
		fs.index(new File("src/main/resources").getPath());
	}
	
	@After
	public void tearDown() throws Exception {
		fs.deleteAllIndex();
		directory.close();
	}
	
	@Test
	public void test() {
		Set<String> result = fs.queryForFilePath("mail");
		result.forEach(s -> System.out.println(s));
		assertFalse(result.isEmpty());
		for (File f : new File("src/main/resources").listFiles()) {
			if (f.isFile()) {
				fs.updateIndex(f.getPath());
				fs.deleteIndex(f.getPath());
			}
		}
	}

}
