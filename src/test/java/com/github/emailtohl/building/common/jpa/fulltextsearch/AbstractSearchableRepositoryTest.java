package com.github.emailtohl.building.common.jpa.fulltextsearch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.jpa.fulltextsearch.AbstractSearchableRepository;
import com.github.emailtohl.building.common.jpa.relationEntities.Relation1;
import com.github.emailtohl.building.site.entities.ForumPost;

public class AbstractSearchableRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	AnnotationConfigApplicationContext ctx = SpringUtils.context;
	@Transactional class ForumFullTextSearch extends AbstractSearchableRepository<ForumPost> {}
	ForumFullTextSearch fs = new ForumFullTextSearch();
	
	@Before
	public void setUp() throws Exception {
		AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(fs, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(fs, "forumFullTextSearch");
		logger.debug("forumFullTextSearch initialized in Spring application context.");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSearch() {
		assertNotNull(fs.entityManagerProxy);
		
	}

	@Test
	public void testAbstractSearchableRepository() {
		assertEquals(ForumPost.class, fs.entityClass);
		logger.debug(Arrays.toString(fs.onFields));
		assertArrayEquals(new String[] {"body", "keywords", "title", "user.description", "user.email", "user.name", "user.username"}, fs.onFields);
	
		class Test extends AbstractSearchableRepository<Relation1>{}
		Test t = new Test();
		assertEquals(Relation1.class, t.entityClass);
		logger.debug(Arrays.toString(t.onFields));
		assertArrayEquals(new String[] {"relation1", "relation2.relation2"}, t.onFields);
	
	}
	
}
