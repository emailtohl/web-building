package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.BaseEntity;
import com.github.emailtohl.building.site.entities.User;
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

	@RequestMapping(value = "findMyApplicationForm", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationForm> findMyApplicationForm(@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<ApplicationForm> page = applicationFormService.findMyApplicationForm(pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	@RequestMapping(value = "findMyApplicationForm/{name}", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationForm> findMyApplicationForm(@PathVariable("name") String name, 
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<ApplicationForm> page = applicationFormService.findByNameLike(name, pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	@RequestMapping(value = "findByStatus/{status}", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationForm> findByStatus(@PathVariable("status") Status status, 
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<ApplicationForm> page = applicationFormService.findByStatus(status, pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@ResponseBody
	public ApplicationForm findByStatus(@PathVariable("id") long id) {
		return applicationFormService.findById(id);
	}
	
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
		public Status status;
		public String cause;
	}
/*	
	@RequestMapping(value = "history", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationForm> history(HistoryForm history, 
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		
		if (!empty(history.applicant)) {
			User u = new User();
			u.setEmail(history.applicant);
		}
		if (!empty(history.handler)) {
			User u = new User();
			u.setEmail(history.handler);
		}
		if (!empty(history.applicant)) {
			User u = new User();
			u.setEmail(history.applicant);
		}
		
		Page<ApplicationForm> page = applicationFormService.historyFindByCreateDateBetween(start, end, pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	private boolean empty(String s) {
		return s == null || s.isEmpty();
	}
	
	public static class HistoryForm implements Serializable {
		private static final long serialVersionUID = 4973257512711350898L;
		public String applicant;
		public String handler;
		public Date start;
		public Date end;
	}*/
}
