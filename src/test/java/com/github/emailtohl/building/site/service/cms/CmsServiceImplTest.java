package com.github.emailtohl.building.site.service.cms;

import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.parent;
import static com.github.emailtohl.building.initdb.PersistenceData.subType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
		assertEquals(a, cmsService.getArticle(a.getId()));
	}

	@Test
	public void testFind() {
		// 从正文中搜索
		Pager<Article> p = cmsService.searchArticles("文章", pageable);
		logger.debug(p.getContent());
//		assertTrue(p.getTotalElements() > 0);
		
		// 从评论中搜索
		p = cmsService.searchArticles("评论", pageable);
		logger.debug(p.getContent());
//		assertTrue(p.getTotalElements() > 0);
	}

	@Test
	public void testArticle() {
		long id = cmsService.saveArticle("test", "test", "test", subType.getName());
		assertTrue(id > 0);
		Article a = cmsService.getArticle(id);
		try {
			assertEquals(emailtohl, a.getAuthor());
			assertEquals(subType, a.getType());
//			assertTrue(cmsService.findTypeByName(subType.getName()).getArticles().contains(a));
			
			cmsService.updateArticle(id, "update", null, "test body", parent.getName());
			a = cmsService.getArticle(id);
			assertEquals("update", a.getTitle());
			assertEquals(parent, a.getType());
			
			assertFalse(cmsService.findTypeByName(subType.getName()).getArticles().contains(a));
//			assertTrue(cmsService.findTypeByName(parent.getName()).getArticles().contains(a));
			
		} finally {
			cmsService.deleteArticle(id);
			cleanAuditData.cleanArticleAudit(id);
		}
	}

	@Test
	public void testComment() {
		long articleId = cmsService.saveArticle("test", "test", "test", "noType");
		long commentId = cmsService.saveComment(articleId, "my comment");
		try {
			Comment c = cmsService.findComment(commentId);
			assertEquals("my comment", c.getContent());
			assertEquals(emailtohl.getUsername(), c.getCritics());
			assertEquals(emailtohl.getIconSrc(), c.getIcon());
			cmsService.updateComment(commentId, "update");
			c = cmsService.findComment(commentId);
			assertEquals("update", c.getContent());
		} finally {
			cmsService.deleteComment(commentId);
			cmsService.deleteArticle(articleId);
			cleanAuditData.cleanArticleAudit(articleId);
		}
	}
	
	@Test
	public void testType() {
		long id = cmsService.saveType("testType", "testType", null);
		try {
			Type t = cmsService.findTypeByName("testType");
			assertNotNull(t);
			cmsService.updateType(id, "updateTestType", "updateTestType", parent.getName());
			t = cmsService.findTypeByName("updateTestType");
			assertEquals("updateTestType", t.getDescription());
			assertEquals(parent, t.getParent());
		} finally {
			cmsService.deleteType(id);
		}
	
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
		List<Type> ls = cmsService.getTypes();
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testClassify() {
		Map<Type, List<Article>> categories = cmsService.classify();
		categories.entrySet().stream().forEach(e -> logger.debug(e));
		assertTrue(categories.size() > 0);
	}

}
