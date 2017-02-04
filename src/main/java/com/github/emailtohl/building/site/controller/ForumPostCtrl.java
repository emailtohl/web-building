package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.site.dto.ForumPostDto;
import com.github.emailtohl.building.site.service.ForumPostService;
/**
 * 论坛控制器
 * @author HeLei
 * @date 2017.02.04
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
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void add(@RequestBody @Valid ForumPostDto form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return;
		}
		this.forumPostService.save(form.getTitle(), form.getKeywords(), form.getBody());
	}
	
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public Pager<ForumPostDto> search(@RequestParam String query,
			@PageableDefault(page = 0, size = 5, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
//		演示时使用完全查询，因为Hibernate Search FullTextQuery getResultSize never matches getResultList().size()
//		return this.forumPostService.find(query, pageable);
		return this.forumPostService.findAllAndPaging(query, pageable);
	}

	@RequestMapping(value = "pager", method = RequestMethod.GET)
	Pager<ForumPostDto> searchPager(@PageableDefault(page = 0, size = 5, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		return this.forumPostService.getPager(pageable);
	}
	
	/**
	 * 特殊情况，管理员删除论坛帖子
	 * @param id
	 */
	@RequestMapping(value = "{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") @Min(1L) long id) {
		this.forumPostService.delete(id);
	}
}
