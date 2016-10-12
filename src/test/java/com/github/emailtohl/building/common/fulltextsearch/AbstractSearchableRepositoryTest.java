package com.github.emailtohl.building.common.fulltextsearch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.entities.ForumPost;

public class AbstractSearchableRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	AnnotationConfigApplicationContext ctx = SpringUtils.context;
	class ForumFullTextSearch extends AbstractSearchableRepository<ForumPost> {}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSearch() {
		fail("Not yet implemented");
	}

	@Test
	public void testAbstractSearchableRepository() {
		ForumFullTextSearch fs = new ForumFullTextSearch();
		assertEquals(ForumPost.class, fs.entityClass);
		logger.debug(Arrays.toString(fs.onFields));
		assertArrayEquals(new String[] {"body", "keywords", "title", "user.description", "user.email", "user.name", "user.username"}, fs.onFields);
	}

}
