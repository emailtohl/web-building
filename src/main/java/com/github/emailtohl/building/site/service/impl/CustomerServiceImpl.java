package com.github.emailtohl.building.site.service.impl;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.site.dao.CustomerRepository;
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
	public Page<Customer> query(String name, String title, String affiliation, Pageable pageable) {
		return customRepository.query(isEmpty(name) ? name : name.trim() + '%', 
				isEmpty(title) ? title : title.trim() + '%', 
				isEmpty(affiliation) ? affiliation : affiliation.trim() + '%', 
				pageable);
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
	public void update(Long id, String name, String title, String affiliation) {
		Customer c = customRepository.getCustomer(id);
		if (!isEmpty(name)) {
			c.setName(name);
		}
		if (!isEmpty(title)) {
			c.setTitle(title);
		}
		if (!isEmpty(affiliation)) {
			c.setAffiliation(affiliation);
		}
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
}
