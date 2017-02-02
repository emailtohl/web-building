package com.github.emailtohl.building.common.lucene;

import java.io.File;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileSearchTest {
	FileSearch fs = new FileSearch("F:\\Server\\apache-tomcat-9.0.0.M15\\wtpwebapps\\web-building-indexBase");

	@Test
	public void test001Index() {
		fs.index(new File("src/main/resources").getAbsolutePath());
	}

	@Test
	public void test002QueryPath() {
		fs.queryPath("mail").forEach(s -> System.out.println(s));
	}

}
