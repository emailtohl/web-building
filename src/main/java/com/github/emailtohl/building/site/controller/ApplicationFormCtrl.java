package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.common.Constant;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.site.dto.ApplicationFormDto;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
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
	public Pager<ApplicationForm> findMyApplicationForm(@PageableDefault(page = 0, size = 10, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
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
	public Pager<ApplicationForm> queryApplicationForm(@RequestParam(name = "name", required = false) String name, 
			@RequestParam(name = "status", required = false) Status status,
			@PageableDefault(page = 0, size = 10, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<ApplicationForm> page = applicationFormService.findByNameAndStatus(name, status, pageable);
		
		return new Pager<ApplicationForm>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	/**
	 * 由于ApplicationForm和ApplicationHandleHistory之间交叉引用，所以gson解析会异常
	 * 所以返回专门存储ApplicationForm的DTO对象，里面可以存储ApplicationHandleHistory信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@ResponseBody
	public ApplicationFormDto get(@PathVariable("id") long id) {
		ApplicationForm af = applicationFormService.findById(id);
		ApplicationFormDto dto = new ApplicationFormDto();
		BeanUtils.copyProperties(af, dto, "applicationHandleHistory");
		try {
			af.getApplicationHandleHistory().forEach(h -> dto.getHistoryList().add(h));
		} catch (RuntimeException e) {// 处理懒加载出现的异常
			logger.debug(e);
			dto.getApplicationHandleHistory().clear();
			applicationFormService.findByApplicationFormIdWhenException(id).forEach(h -> dto.getHistoryList().add(h));
		}
		return dto;
	}
	
	/**
	 * 提交申请表
	 * @param af
	 * @param e
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ApplicationForm> add(@RequestBody @Valid ApplicationForm af, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		long id = applicationFormService.application(af.getName(), af.getDescription());
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/applicationForm/{id}")
				.buildAndExpand(id).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);
		return new ResponseEntity<>(af, headers, HttpStatus.CREATED);
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
	
	private final Pattern p = Pattern.compile(Constant.PATTERN_DATE);
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 查看申请表处理历史
	 * @param history
	 * @param pageable
	 * @return
	 * @throws ParseException 时间解析异常
	 */
	@RequestMapping(value = "history", method = RequestMethod.GET)
	@ResponseBody
	public Pager<ApplicationHandleHistory> history(
			@RequestParam(required = false) String applicant, 
			@RequestParam(required = false) String handler,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Status status, 
			@RequestParam(required = false, defaultValue = "") String start,
			@RequestParam(required = false, defaultValue = "") String end,
			@PageableDefault(page = 0, size = 10, sort = BaseEntity.MODIFY_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) throws ParseException {
		
		Date startTime = null, endTime = null;
		Matcher m = p.matcher(start);
		if (m.find()) {
			startTime = sdf.parse(m.group(0));
		}
		m = p.matcher(end);
		if (m.find()) {
			endTime = sdf.parse(m.group(0));
			Instant i = endTime.toInstant();
			endTime = Date.from(i.plus(Duration.ofDays(1)));
		}
		Page<ApplicationHandleHistory> page = applicationFormService.history(applicant, handler, name, status, startTime, endTime, pageable);
		
		return new Pager<ApplicationHandleHistory>(page.getContent(), page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	/**
	 * 查看某个申请表的处理历史记录
	 * @param history
	 * @param pageable
	 * @return
	 * @throws ParseException 时间解析异常
	 */
	@RequestMapping(value = "history/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ApplicationHandleHistory getHistoryById(@PathVariable("id") long id) throws ParseException {
		return applicationFormService.getHistoryById(id);
	}
	
	/**
	 * 特殊情况，允许具有APPLICATION_FORM_DELETE权限的用户删除申请单
	 * 由于数据完整性，删除申请单的同时将删除所有申请单记录以及用户相关的记录
	 * @param id
	 */
	@RequestMapping(value = "{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") @Min(1L) long id) {
		applicationFormService.delete(id);
	}
}
