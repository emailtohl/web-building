package com.github.emailtohl.building.site.dao;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;

public class ForumPostRepositoryTest {
	AnnotationConfigApplicationContext ctx = SpringUtils.context;
	ForumPostRepository forumPostRepository = ctx.getBean(ForumPostRepository.class);
	UserRepository UserRepository = ctx.getBean(UserRepository.class);
	static final Logger logger = LogManager.getLogger();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSearch() {
		ForumPost p1 = new ForumPost(), p2 = new ForumPost(), p3 = new ForumPost();
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

}
