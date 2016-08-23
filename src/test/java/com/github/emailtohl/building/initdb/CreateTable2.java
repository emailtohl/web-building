package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.add;
import static com.github.emailtohl.building.initdb.PersistenceData.admin;
import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.company;
import static com.github.emailtohl.building.initdb.PersistenceData.delete;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.employee;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.manager;
import static com.github.emailtohl.building.initdb.PersistenceData.product;
import static com.github.emailtohl.building.initdb.PersistenceData.qa;
import static com.github.emailtohl.building.initdb.PersistenceData.query;
import static com.github.emailtohl.building.initdb.PersistenceData.update;
import static com.github.emailtohl.building.initdb.PersistenceData.user;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.bootspring.SpringUtils;

@Service
@Transactional
public class CreateTable2 {
	private static AnnotationConfigApplicationContext ctx = SpringUtils.ctx;
	@PersistenceContext
	protected EntityManager entityManager;
	
	public void init() {
		entityManager.persist(company);
		entityManager.persist(product);
		entityManager.persist(qa);
		entityManager.persist(add);
		entityManager.persist(delete);
		entityManager.persist(update);
		entityManager.persist(query);
		entityManager.persist(admin);
		entityManager.persist(employee);
		entityManager.persist(manager);
		entityManager.persist(user);
		entityManager.persist(emailtohl);
		entityManager.persist(foo);
		entityManager.persist(bar);
	}
	public static void main(String[] args) throws Exception {
		CreateTable2 ct = ctx.getBean(CreateTable2.class);
		ct.init();
		System.exit(0);
	}

}
