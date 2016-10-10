package com.github.emailtohl.building.site.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ForumPostService;

/**
 * 论坛控制器
 * 
 * @author HeLei
 */
@Controller
public class ForumPostController {
	@Inject
	ForumPostService forumPostService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String form(Map<String, Object> model) {
		model.put("added", null);
		model.put("addForm", new PostForm());
		return "add";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String add(Map<String, Object> model, PostForm form) {
		User user = this.forumPostService.getUser(form.getUser());
		if (user == null)
			throw new IllegalArgumentException("User does not exist.");

		ForumPost post = new ForumPost();
		post.setUser(user);
		post.setTitle(form.getTitle());
		post.setBody(form.getBody());
		post.setKeywords(form.getKeywords());

		this.forumPostService.save(post);

		model.put("added", post.getId());
		model.put("addForm", new PostForm());
		return "add";
	}

	@RequestMapping(value = "search")
	public String search(Map<String, Object> model) {
		model.put("searchPerformed", false);
		model.put("searchForm", new SearchForm());

		return "search";
	}

	@RequestMapping(value = "search", params = "query")
	public String search(Map<String, Object> model, SearchForm form, Pageable pageable) {
		if (form.getQuery() == null || form.getQuery().trim().length() == 0)
			model.put("searchPerformed", false);
		else {
			model.put("searchPerformed", true);
			model.put("results", this.forumPostService.search(form.getQuery(), pageable));
		}

		return "search";
	}

	public static class PostForm {
		private String user;

		private String title;

		private String body;

		private String keywords;

		public String getUser() {
			return this.user;
		}

		public void setUser(String user) {
			this.user = user;
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
