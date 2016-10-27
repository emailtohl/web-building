package com.github.emailtohl.building.site.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchResult;
import com.github.emailtohl.building.site.dto.ForumPostDto;
import com.github.emailtohl.building.site.entities.BaseEntity;
import com.github.emailtohl.building.site.service.ForumPostService;

/**
 * 论坛控制器
 * 
 * @author HeLei
 */
@RestController
@RequestMapping("forum")
public class ForumPostCtrl {
	private static final Logger logger = LogManager.getLogger();
	
	ForumPostService forumPostService;
	
	@Inject
	public ForumPostCtrl(ForumPostService forumPostService) {
		super();
		this.forumPostService = forumPostService;
	}
	
	/*	@RequestMapping(value = "", method = RequestMethod.GET)
	public String form(Map<String, Object> model) {
		model.put("added", null);
		model.put("addForm", new PostForm());
		return "add";
	}
*/
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void add(@RequestBody @Valid ForumPostDto form, Errors e) {
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
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public Pager<SearchResult<ForumPostDto>> search(@RequestParam String query,
			@PageableDefault(page = 0, size = 5, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		return this.forumPostService.search(query, pageable);
	}

	@RequestMapping(value = "pager", method = RequestMethod.GET)
	Pager<SearchResult<ForumPostDto>> searchPager(@PageableDefault(page = 0, size = 5, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Pager<ForumPostDto> p = forumPostService.getPager(pageable);
		List<SearchResult<ForumPostDto>> ls = new ArrayList<SearchResult<ForumPostDto>>();
		p.getContent().forEach(dto -> {
			ls.add(new SearchResult<ForumPostDto>(dto, 1));
		});
		return new Pager<SearchResult<ForumPostDto>>(ls, p.getTotalElements(), pageable.getPageNumber(), p.getPageSize());
	}
	
}
