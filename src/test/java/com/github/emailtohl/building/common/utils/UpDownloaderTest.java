package com.github.emailtohl.building.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class UpDownloaderTest {
	private static final Logger logger = LogManager.getLogger();
	String basePath = System.getProperty("user.home") + "/test";
	String relativePath = "abc/def";
	String testFile = "testFile";
	byte[] bin = new byte[1024];
	String absolutePath = new File(basePath, relativePath).getAbsolutePath();
	Part part = mock(Part.class);
	InputStream in = new ByteArrayInputStream(bin);
	OutputStream out = new ByteArrayOutputStream();
	HttpServletResponse response = mock(HttpServletResponse.class);
	ServletOutputStream sos = mock(ServletOutputStream.class);
	UpDownloader upDownloader = new UpDownloader(basePath);
	
	@Before
	public void setUp() throws Exception {
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		doAnswer(answer).when(part).write(absolutePath);
		when(response.getOutputStream()).thenReturn(sos);
		doAnswer(answer).when(sos).write(bin);
	}

	@After
	public void tearDown() throws Exception {
		File f = new File(basePath);
		if (f.exists())
			upDownloader.deleteDir(basePath);
	}

	@Test
	public void testUploadStringPart() throws IOException {
		assertEquals(absolutePath, upDownloader.upload(relativePath, part));
	}

	@Test
	public void testUploadStringByteArray() throws IOException {
		assertFalse(upDownloader.upload(testFile, bin).isEmpty());
	}

	@Test
	public void testUploadStringInputStream() throws IOException {
		assertFalse(upDownloader.upload(testFile, in).isEmpty());
	}

	@Test
	public void testGetFile() throws IOException {
		upDownloader.upload(testFile, bin);
		byte[] b = upDownloader.getFile(testFile);
		assertTrue(Arrays.equals(bin, b));
	}

	@Test
	public void testGetAbsolutePath() {
		assertEquals(absolutePath, upDownloader.getAbsolutePath(relativePath));
	}

	@Test
	public void testDownloadStringHttpServletResponse() throws FileNotFoundException, IOException {
		upDownloader.upload(testFile, bin);
		upDownloader.download(testFile, response);
	}

	@Test
	public void testDownloadStringOutputStream() throws IOException {
		upDownloader.upload(testFile, bin);
		upDownloader.download(testFile, out);
	}

}
