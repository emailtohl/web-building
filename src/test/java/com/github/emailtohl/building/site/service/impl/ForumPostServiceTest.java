package com.github.emailtohl.building.site.service.impl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.dao.SearchResult;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ForumPostService;
/**
 * 全文搜索测试
 * 先存储论坛帖子，然后再通过索引查询
 * 
 * @author HeLei
 */
public class ForumPostServiceTest {
	static final Logger logger = LogManager.getLogger();
	AnnotationConfigApplicationContext ctx = SpringUtils.context;
	ForumPostService forumPostService = ctx.getBean(ForumPostService.class);
	UserRepository userRepository = ctx.getBean(UserRepository.class);
	ForumPost p1 = new ForumPost(), p2 = new ForumPost(), p3 = new ForumPost();
	
	@Before
	public void setUp() throws Exception {
		p1.setTitle("emailtohl's post");
		p1.setKeywords("first emailtohl");
		p1.setBody("hl's first post hello forum");
		User emailtohl = userRepository.findByEmail("emailtohl@163.com");
		p1.setUser(emailtohl);
		
		p2.setTitle("foo's post");
		p2.setKeywords("first foo");
		p2.setBody("foo's first post hello forum");
		User foo = userRepository.findByEmail("foo@test.com");
		p2.setUser(foo);
		
		p3.setTitle("bar's post");
		p3.setKeywords("first bar");
		p3.setBody("bar's first post hello forum");
		User bar = userRepository.findByEmail("bar@test.com");
		p3.setUser(bar);
		
		forumPostService.save(p1);
		forumPostService.save(p2);
		forumPostService.save(p3);
	}

	@Test
	public void testSearch() {
		Page<SearchResult<ForumPost>> p = forumPostService.search("first", new PageRequest(0, 20));
		List<String> ls = Arrays.asList("emailtohl's post", "foo's post", "bar's post");
		for (SearchResult<ForumPost> s : p.getContent()) {
			System.out.println(s.getEntity().getTitle());
			System.out.println(s.getEntity().getKeywords());
			System.out.println(s.getEntity().getBody());
			System.out.println(s.getEntity().getUser().getEmail());
			System.out.println(s.getEntity().getUser().getName());
			System.out.println(s.getRelevance());
			assertTrue(ls.contains(s.getEntity().getTitle()));
		}
		
		p = forumPostService.search("emailtohl@163.com", new PageRequest(0, 20));
		for (SearchResult<ForumPost> s : p.getContent()) {
			System.out.println(s.getEntity().getTitle());
			System.out.println(s.getEntity().getKeywords());
			System.out.println(s.getEntity().getBody());
			System.out.println(s.getEntity().getUser().getEmail());
			System.out.println(s.getEntity().getUser().getName());
			System.out.println(s.getRelevance());
			assertEquals("emailtohl's post", s.getEntity().getTitle());
		}
	}

	@After
	public void clean() {
		forumPostService.delete(p1);
		forumPostService.delete(p2);
		forumPostService.delete(p3);
	}
}
