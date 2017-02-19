package com.github.emailtohl.building.site.service.cms;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

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
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.cms.Comment;
import com.github.emailtohl.building.site.entities.cms.Type;
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
	@Inject CleanAuditData cleanAuditData;
	@Inject SecurityContextManager securityContextManager;

	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
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
	public void testArticle() {
		long id = cmsService.saveArticle("test", "test", "test", "noType");
		assertTrue(id > 0);
		Article a = cmsService.findArticle(id);
		assertEquals(PersistenceData.emailtohl, a.getAuthor());
		cmsService.updateArticle(id, "update", null, "test body", null);
		a = cmsService.findArticle(id);
		assertEquals("update", a.getTitle());
		assertNull(a.getType());
		cmsService.deleteArticle(id);
		a = cmsService.findArticle(id);
		assertNull(a);
		cleanAuditData.cleanArticleAudit(id);
	}

	@Test
	public void testComment() {
		long articleId = cmsService.saveArticle("test", "test", "test", "noType");
		long commentId = cmsService.saveComment(articleId, "my comment");
		Comment c = cmsService.findComment(commentId);
		assertEquals("my comment", c.getContent());
		assertEquals(PersistenceData.emailtohl.getUsername(), c.getCritics());
		assertEquals(PersistenceData.emailtohl.getIconSrc(), c.getIcon());
		cmsService.updateComment(commentId, "update");
		c = cmsService.findComment(commentId);
		assertEquals("update", c.getContent());
		cmsService.deleteComment(commentId);
		cmsService.deleteArticle(articleId);
		cleanAuditData.cleanArticleAudit(articleId);
	}

	@Test
	public void testRecentArticles() {
		List<Article> ls = cmsService.recentArticles();
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testRecentComments() {
		List<Comment> ls = cmsService.recentComments();
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testGetArticleTypes() {
		List<Type> ls = cmsService.getArticleTypes();
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testClassify() {
		Map<Type, List<Article>> categories = cmsService.classify();
		categories.entrySet().stream().forEach(e -> logger.debug(e));
		assertTrue(categories.size() > 0);
	}

	@Test
	public void testGetWebPage() {
		WebPage wp = cmsService.getWebPage("文章");
		logger.debug(wp.categories);
		logger.debug(wp.recentArticles);
		logger.debug(wp.recentComments);
		assertNotNull(wp);
	}

}
