package com.github.emailtohl.building.common.jpa.fulltextsearch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.common.jpa.relationEntities.Relation1;
import com.github.emailtohl.building.site.entities.ForumPost;

public class AbstractSearchableRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	AnnotationConfigApplicationContext context = Spring.context;
	class ForumFullTextSearch extends AbstractSearchableRepository<ForumPost> {}
	ForumFullTextSearch forumFullTextSearch = new ForumFullTextSearch();
	class TestFindByField extends AbstractSearchableRepository<Relation1>{}
	
	/**
	 * 需要在事务环境中使用，所以在具体类中进行测试
	 */
	@Test
	public void testSearch() {
		AutowireCapableBeanFactory factory = Spring.context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(forumFullTextSearch, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(forumFullTextSearch, "forumFullTextSearch");
		assertNotNull(forumFullTextSearch.entityManagerProxy);
	}

	@Test
	public void testAbstractSearchableRepository() {
		assertEquals(ForumPost.class, forumFullTextSearch.entityClass);
		logger.debug(Arrays.toString(forumFullTextSearch.onFields));
		assertArrayEquals(new String[] {"body", "keywords", "title", "user.description", "user.email", "user.name", "user.username"}, forumFullTextSearch.onFields);
	
		TestFindByField t = new TestFindByField();
		assertEquals(Relation1.class, t.entityClass);
		logger.debug(Arrays.toString(t.onFields));
		assertArrayEquals(new String[] {"relation1", "relation2.relation2"}, t.onFields);
	
	}
	
}
