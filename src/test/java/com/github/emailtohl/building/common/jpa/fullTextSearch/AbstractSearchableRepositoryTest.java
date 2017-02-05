package com.github.emailtohl.building.common.jpa.fullTextSearch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.relationEntities.Relation1;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.entities.ForumPost;
/**
 * Hibernate search组件测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class AbstractSearchableRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ApplicationContext context;
	class ForumFullTextSearch extends AbstractSearchableRepository<ForumPost> {}
	ForumFullTextSearch forumFullTextSearch = new ForumFullTextSearch();
	class TestFindByField extends AbstractSearchableRepository<Relation1>{}
	
	/**
	 * 需要在事务环境中使用，所以在具体类中进行测试
	 */
	@Test
	public void testInitialize() {
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(forumFullTextSearch, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(forumFullTextSearch, "forumFullTextSearch");
	}

	@Test
	public void testAbstractSearchableRepository() {
		assertEquals(ForumPost.class, forumFullTextSearch.getEntityClass());
		logger.debug(Arrays.toString(forumFullTextSearch.onFields));
		assertArrayEquals(new String[] {"body", "keywords", "title", "user.description", "user.email", "user.name", "user.username"}, forumFullTextSearch.onFields);
	
		TestFindByField t = new TestFindByField();
		assertEquals(Relation1.class, t.getEntityClass());
		logger.debug(Arrays.toString(t.onFields));
		assertArrayEquals(new String[] {"relation1", "relation2.relation2"}, t.onFields);
	
	}
	
}
