package com.github.emailtohl.building.site.controller;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dao.SearchResult;
import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ForumPostService;

/**
 * 论坛控制器
 * 
 * @author HeLei
 */
@RestController
@RequestMapping("forum")
public class ForumPostController {
	@Inject
	ForumPostService forumPostService;

/*	@RequestMapping(value = "", method = RequestMethod.GET)
	public String form(Map<String, Object> model) {
		model.put("added", null);
		model.put("addForm", new PostForm());
		return "add";
	}
*/
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void add(PostForm form) {
		User user = this.forumPostService.getUser(form.getEmail());
		if (user == null)
			throw new IllegalArgumentException("User does not exist.");

		ForumPost post = new ForumPost();
		post.setUser(user);
		post.setTitle(form.getTitle());
		post.setBody(form.getBody());
		post.setKeywords(form.getKeywords());

		this.forumPostService.save(post);
	}
/*
	@RequestMapping(value = "search")
	public String search(Map<String, Object> model) {
		model.put("searchPerformed", false);
		model.put("searchForm", new SearchForm());

		return "search";
	}
*/
	@RequestMapping(value = "search", params = "query")
	public Pager<SearchResult<ForumPost>> search(SearchForm form, Pageable pageable) {
		Pager<SearchResult<ForumPost>> p = null;
		if (form.getQuery() != null && form.getQuery().trim().length() != 0) {
			Page<SearchResult<ForumPost>> page = this.forumPostService.search(form.getQuery(), pageable);
			p = new Pager<>(page.getContent(), page.getTotalElements(), page.getSize());
		}
		return p;
	}

	public static class PostForm {
		private String username;
		private String email;
		private String title;
		private String body;
		private String keywords;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBody() {
			return this.body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getKeywords() {
			return this.keywords;
		}

		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}
	}

	public static class SearchForm {
		private String query;

		public String getQuery() {
			return this.query;
		}

		public void setQuery(String query) {
			this.query = query;
		}
	}
}
