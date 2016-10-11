package com.github.emailtohl.building.site.controller;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dao.SearchResult;
import com.github.emailtohl.building.site.dto.ForumPostDto;
import com.github.emailtohl.building.site.service.ForumPostService;

/**
 * 论坛控制器
 * 
 * @author HeLei
 */
@RestController
@RequestMapping("forum")
public class ForumPostController {
	private static final Logger logger = LogManager.getLogger();
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
	public void add(@Valid ForumPostDto form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return;
		}
		this.forumPostService.save(form.getEmail(), form);
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
	public Pager<SearchResult<ForumPostDto>> search(SearchForm form, Pageable pageable) {
		Pager<SearchResult<ForumPostDto>> p = null;
		if (form.getQuery() != null && form.getQuery().trim().length() != 0) {
			p = this.forumPostService.search(form.getQuery(), pageable);
		}
		return p;
	}

	@RequestMapping(value = "pager", method = RequestMethod.GET)
	Pager<ForumPostDto> getPager(Pageable pageable) {
		return forumPostService.getPager(pageable);
	}
	
	public static class PostForm {
		private String username;
		@NotNull
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
