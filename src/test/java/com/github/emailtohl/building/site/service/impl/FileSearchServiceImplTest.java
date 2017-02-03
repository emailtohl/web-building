package com.github.emailtohl.building.site.service.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.emailtohl.building.common.utils.TextUtil;
import com.github.emailtohl.building.common.utils.UpDownloader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileSearchServiceImplTest {
	FileSearchServiceImpl fs = new FileSearchServiceImpl();
	{
		fs.init("../testIndexBase");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		File dir = new File("../testIndexBase").getCanonicalFile();
		System.out.println(dir);
		FileUtils.deleteQuietly(dir);
		
		UpDownloader upDownloader = new UpDownloader(dir);
		upDownloader.deleteDir(dir.getAbsolutePath());
	}

	@Test
	public void test001Index() {
		fs.index(new File("src/main/resources").getAbsolutePath());
	}

	@Test
	public void test002QueryPath() {
		fs.queryPath("mail").forEach(s -> System.out.println(s));
	}

}
