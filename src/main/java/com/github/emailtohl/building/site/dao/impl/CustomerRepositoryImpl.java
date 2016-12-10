package com.github.emailtohl.building.site.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.common.jpa.fullTextSearch.AbstractSearchableRepository;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion.Operator;
import com.github.emailtohl.building.site.dao.CustomerRepository;
import com.github.emailtohl.building.site.entities.Customer;

/**
 * 客户管理数据访问接口实现类
 * @author HeLei
 */
@Repository
public class CustomerRepositoryImpl extends AbstractSearchableRepository<Customer> implements CustomerRepository {

	@Override
	public Page<Customer> query(String name, String title, String affiliation, Pageable pageable) {
		List<Criterion> ls = new ArrayList<>();
		if (!isEmpty(name)) {
			ls.add(new Criterion("name", Operator.LIKE, name));
		}
		if (!isEmpty(title)) {
			ls.add(new Criterion("title", Operator.LIKE, title));
		}
		if (!isEmpty(affiliation)) {
			ls.add(new Criterion("affiliation", Operator.LIKE, affiliation));
		}
		Page<Customer> p = search(ls, pageable);
		return p;
	}

	@Override
	public Customer getCustomer(Long id) {
		return entityManager.find(entityClass, id);
	}

	@Override
	public void merge(Customer c) {
		entityManager.merge(c);
	}

	@Override
	public void delete(Long id) {
		Customer c = entityManager.find(entityClass, id);
		entityManager.remove(c);
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
}
