package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.BaseEntity;
import com.github.emailtohl.building.site.service.ApplicationFormService;

@Controller
@RequestMapping("applicationForm")
public class ApplicationFormCtrl {
	private static final Logger logger = LogManager.getLogger();
	ApplicationFormService applicationFormService;
	
	@Inject
	public void setApplicationFormService(ApplicationFormService applicationFormService) {
		this.applicationFormService = applicationFormService;
	}

	/**
	 * 查询自己的申请表
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "mine", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationForm> findMyApplicationForm(@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<ApplicationForm> page = applicationFormService.findMyApplicationForm(pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	/**
	 * 查询所有申请表，用于处理申请表所用
	 * @param name
	 * @param status
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationForm> queryApplicationForm(@RequestParam("name") String name, @RequestParam("status") Status status,
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<ApplicationForm> page = applicationFormService.findByNameAndStatus(name, status, pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@ResponseBody
	public ApplicationForm get(@PathVariable("id") long id) {
		return applicationFormService.findById(id);
	}
	
	/**
	 * 提交申请表
	 * @param form
	 * @param e
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ApplicationForm> add(@RequestBody @Valid ApplicationForm form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		long id = applicationFormService.application(form);
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/applicationForm/{id}")
				.buildAndExpand(id).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);
		return new ResponseEntity<>(form, headers, HttpStatus.CREATED);
	}
	
	/**
	 * 修改申请表状态，处理申请表所用
	 * @param id
	 * @param form
	 * @param e
	 * @return
	 */
	@RequestMapping(value = "{id}", method = PUT)
	public ResponseEntity<Void> transit(@PathVariable("id") @Min(1L) long id, @Valid @RequestBody Form form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		applicationFormService.transit(id, form.status, form.cause);
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	public static class Form implements Serializable {
		private static final long serialVersionUID = 4973257512711350898L;
		@NotNull public String name;
		@NotNull public String description;
		public Status status;
		public String cause;
	}
	
	/**
	 * 查看申请表处理历史
	 * @param history
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "history", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationHandleHistory> history(@RequestBody HistoryForm history, 
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		
		Page<ApplicationHandleHistory> page = applicationFormService.history(history.applicant, history.handler, history.status, history.start, history.end, pageable);
		
		return new Pager<ApplicationHandleHistory>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	public static class HistoryForm implements Serializable {
		private static final long serialVersionUID = 4973257512711350898L;
		public String applicant;
		public String handler;
		public Status status;
		public Date start;
		public Date end;
	}
}
