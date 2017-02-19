package com.github.emailtohl.building.site.service.cms;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.stub.SecurityContextManager;

/**
 * cms的服务层实现
 * @author HeLei
 * @data 2017.02.19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class CmsServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	final Pageable pageable = new PageRequest(0, 20);
	@Inject CmsService cmsService;
	@Inject SecurityContextManager securityContextManager;

	@Before
	public void setUp() throws Exception {
		securityContextManager.setFoo();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindArticle() {
		List<Article> ls = cmsService.recentArticles();
		Article a = ls.get(0);
		assertEquals(a, cmsService.findArticle(a.getId()));
	}

	@Test
	public void testFind() {
		Pager<Article> p = cmsService.find("文章", pageable);
		logger.debug(p.getContent());
		assertTrue(p.getTotalElements() > 0);
	}

	@Test
	public void testSaveArticleStringStringStringType() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveArticleStringStringStringStringType() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateArticle() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteArticle() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveCommentStringLongString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveCommentLongString() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testRecentArticles() {
		fail("Not yet implemented");
	}

	@Test
	public void testRecentComments() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetArticleTypes() {
		fail("Not yet implemented");
	}

	@Test
	public void testClassify() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWebPage() {
		fail("Not yet implemented");
	}

}
