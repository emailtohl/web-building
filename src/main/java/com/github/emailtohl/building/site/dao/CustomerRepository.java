package com.github.emailtohl.building.site.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchableRepository;
import com.github.emailtohl.building.site.entities.Customer;

/**
 * 只查询客户的数据访问接口
 * @author HeLei
 */
public interface CustomerRepository extends SearchableRepository<Customer> {
	Page<Customer> query(String name, String title, String affiliation, Pageable pageable);

	Customer getCustomer(Long id);
	
	void merge(Customer c);

	void delete(Long id);
	
	List<Customer> findAll();
}
