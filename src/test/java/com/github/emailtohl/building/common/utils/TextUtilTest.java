package com.github.emailtohl.building.common.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class TextUtilTest {
	TextUtil util = new TextUtil();

	@Test
	public void testAvailableCharsets() {
		System.out.println(util.charsets);
	}
	
	@Test
	public void testWriteText() {
		File f = new File("text.txt");
		try {
			util.writeText("text.txt", "text", "GB2312");
			assertTrue(f.exists());
		} finally {
			if (f.exists())
				f.delete();
		}
	}

	@Test
	public void testGetText() {
		String s = util.getText("README.md", "UTF-8");
		assertNotNull(s);
	}

}
