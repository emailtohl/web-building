package com.github.emailtohl.building.site.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
		Set<Criterion> set = new HashSet<>();
		if (!isEmpty(name)) {
			set.add(new Criterion("name", Operator.LIKE, name));
		}
		if (!isEmpty(title)) {
			set.add(new Criterion("title", Operator.LIKE, title));
		}
		if (!isEmpty(affiliation)) {
			set.add(new Criterion("affiliation", Operator.LIKE, affiliation));
		}
		Page<Customer> p = search(set, pageable);
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

	@Override
	public List<Customer> findAll() {
		CriteriaBuilder b = entityManager.getCriteriaBuilder();
		CriteriaQuery<Customer> q = b.createQuery(entityClass);
		Root<Customer> r = q.from(entityClass);
		return entityManager.createQuery(q.select(r)).getResultList();
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
}
