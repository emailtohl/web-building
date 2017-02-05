package com.github.emailtohl.building.common.ztree;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.building.common.utils.UpDownloader;
import com.google.gson.Gson;
/**
 * ztree数据模型的测试
 * @author HeLei
 * @date 2017.02.04
 */
public class ZtreeNodeTest {
	File test_root = new File("test_root");
	File sub1 = new File(test_root, "sub1"),
		 sub2 = new File(test_root, "sub2"),
		 sub3 = new File(test_root, "sub3");
	File sub1_1 = new File(sub1, "sub1_1"),
		 sub1_2 = new File(sub1, "sub1_2");
	File sub2_1 = new File(sub2, "sub2_1"),
		 sub2_2 = new File(sub2, "sub2_2");
	
	@Before
	public void setUp() throws Exception {
		sub1.mkdirs();
		sub2.mkdirs();
		sub3.createNewFile();
		sub1_1.createNewFile();
		sub1_2.createNewFile();
		sub2_1.createNewFile();
		sub2_2.createNewFile();
	}

	@After
	public void tearDown() throws Exception {
		new UpDownloader("test_root").deleteDir(test_root.getAbsolutePath());
	}

	@Test
	public void testNewInstance() {
		ZtreeNode n = ZtreeNode.newInstance(test_root);
		Gson gson = new Gson();
		String json = gson.toJson(n);
		System.out.println(json);
		ZtreeNode nn = gson.fromJson(json, ZtreeNode.class);
		System.out.println(nn);
		assertEquals(nn, n);
		long rootid = nn.getId();
		long pid = nn.getChildren().iterator().next().getPid();
		assertEquals(rootid, pid);
	}

	@Test
	public void testSetOpen() {
		ZtreeNode n = ZtreeNode.newInstance(test_root);
		String path = sub2_2.getPath();
		n.setOpen(path);
		assertTrue(n.isOpen());
		for (ZtreeNode sub : n.getChildren()) {
			if ("sub2".equals(sub.getName())) {
				assertTrue(sub.isOpen());
			} else {
				assertFalse(sub.isOpen());
			}
		}
	}
}
