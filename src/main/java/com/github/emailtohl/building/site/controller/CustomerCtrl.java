package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.Serializable;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.service.CustomerService;

/**
 * 用户管理的控制器
 * @author HeLei
 */
@RestController
@RequestMapping("customer")
public class CustomerCtrl {
	@Inject CustomerService customerService;
	
	/**
	 * 根据用户名和公司进行组合查询
	 * @param title
	 * @param affiliation
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "pager", method = GET, produces = "application/json; charset=utf-8")
	Pager<Customer> query(@RequestParam(required = false) String name, @RequestParam(required = false) String title, @RequestParam(required = false) String affiliation, 
			@PageableDefault(page = 0, size = 20, sort = {"name", "title", "affiliation"}, direction = Direction.DESC) Pageable pageable) {
		Page<Customer> p = customerService.query(name, title, affiliation, pageable);
		return new Pager<Customer>(p.getContent(), p.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	/**
	 * 获取客户详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "{id}", method = GET, produces = "application/json; charset=utf-8")
	Customer getCustomer(@PathVariable("id") Long id) {
		return customerService.getCustomer(id);
	}
	
	/**
	 * 修改客户的基本资料
	 * @param id
	 * @param name
	 * @param title
	 * @param affiliation
	 */
	@RequestMapping(value = "{id}", method = PUT, produces = "application/json; charset=utf-8")
	void update(@PathVariable("id") Long id, @RequestBody Form form) {
		customerService.update(id, form.getName(), form.getTitle(), form.getAffiliation());
	}
	
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public static class Form implements Serializable {
		private static final long serialVersionUID = 3551766794688095565L;
		String name;
		String title;
		String affiliation;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAffiliation() {
			return affiliation;
		}
		public void setAffiliation(String affiliation) {
			this.affiliation = affiliation;
		}
	}
}
