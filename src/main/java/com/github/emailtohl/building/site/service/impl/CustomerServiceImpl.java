package com.github.emailtohl.building.site.service.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dao.CustomerRepository;
import com.github.emailtohl.building.site.entities.BaseEntity;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.service.CustomerService;
/**
 * 客户管理（CRM）服务的实现
 * @author HeLei
 */
@Service
public class CustomerServiceImpl implements CustomerService {
	@Inject CustomerRepository customRepository;

	@Override
	public Pager<Customer> query(String name, String title, String affiliation, Pageable pageable) {
		Page<Customer> page = customRepository.query(isEmpty(name) ? name : name.trim() + '%', 
				isEmpty(title) ? title : title.trim() + '%', 
				isEmpty(affiliation) ? affiliation : affiliation.trim() + '%', 
				pageable);
		List<Customer> ls = new ArrayList<>();
		page.getContent().forEach((p/*持久化*/ -> {
			Customer t = new Customer();// 瞬时
			BeanUtils.copyProperties(p, t, "password", "icon", "roles");
			ls.add(t);
		}));
		return new Pager<Customer>(ls, page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}

	@Override
	public Customer getCustomer(Long id) {
		Customer t = new Customer();// 瞬时
		Customer p = customRepository.getCustomer(id);// 持久化
		if (p != null) {
			BeanUtils.copyProperties(p, t, "password", "icon", "roles");
		}
		return t;
	}

	@Override
	public void update(Long id, Customer customer) {
		Customer c = customRepository.getCustomer(id);
		BeanUtils.copyProperties(customer, c, BaseEntity.ID_PROPERTY_NAME, BaseEntity.CREATE_DATE_PROPERTY_NAME, BaseEntity.MODIFY_DATE_PROPERTY_NAME, "email", "username", "roles", "password", "enabled");
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	@Override
	public void excel(OutputStream out) {
		List<Customer> ls = customRepository.findAll();
		
	}
	
}
