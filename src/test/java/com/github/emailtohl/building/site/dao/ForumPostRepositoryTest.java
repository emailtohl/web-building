package com.github.emailtohl.building.site.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;

public class ForumPostRepositoryTest {
	static final Logger logger = LogManager.getLogger();
	AnnotationConfigApplicationContext ctx = SpringUtils.context;
	ForumPostRepository forumPostRepository = ctx.getBean(ForumPostRepository.class);
	UserRepository UserRepository = ctx.getBean(UserRepository.class);
	ForumPost p1 = new ForumPost(), p2 = new ForumPost(), p3 = new ForumPost();
	
	@Before
	public void setUp() throws Exception {
		p1.setTitle("emailtohl's post");
		p1.setKeywords("first emailtohl");
		p1.setBody("hl's post first post");
		User emailtohl = UserRepository.findByEmail("emailtohl@163.com");
		p1.setUser(emailtohl);
		
		p2.setTitle("foo's post");
		p2.setKeywords("first foo");
		p2.setBody("foo's post first post");
		User foo = UserRepository.findByEmail("foo@test.com");
		p2.setUser(foo);
		
		p3.setTitle("bar's post");
		p3.setKeywords("first bar");
		p3.setBody("bar's post first post");
		User bar = UserRepository.findByEmail("bar@test.com");
		p3.setUser(bar);
		
		forumPostRepository.save(p1);
		forumPostRepository.save(p2);
		forumPostRepository.save(p3);
	}

	@Test
	public void testSearch() {
		Page<SearchResult<ForumPost>> p = forumPostRepository.search("first", new PageRequest(0, 20));
		for (SearchResult<ForumPost> s : p.getContent()) {
			System.out.println(s.getEntity());
			System.out.println(s.getRelevance());
		}
	}

	@After
	public void clean() {
		forumPostRepository.delete(p1);
		forumPostRepository.delete(p2);
		forumPostRepository.delete(p3);
	}
}
