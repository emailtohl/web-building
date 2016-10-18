package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.admin;
import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.company;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.employee;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.manager;
import static com.github.emailtohl.building.initdb.PersistenceData.product;
import static com.github.emailtohl.building.initdb.PersistenceData.qa;
import static com.github.emailtohl.building.initdb.PersistenceData.user;
import static com.github.emailtohl.building.initdb.PersistenceData.user_create_ordinary;
import static com.github.emailtohl.building.initdb.PersistenceData.user_create_special;
import static com.github.emailtohl.building.initdb.PersistenceData.user_delete;
import static com.github.emailtohl.building.initdb.PersistenceData.user_disable;
import static com.github.emailtohl.building.initdb.PersistenceData.user_enable;
import static com.github.emailtohl.building.initdb.PersistenceData.user_read_all;
import static com.github.emailtohl.building.initdb.PersistenceData.user_read_self;
import static com.github.emailtohl.building.initdb.PersistenceData.user_update_all;
import static com.github.emailtohl.building.initdb.PersistenceData.user_update_self;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.bootspring.Spring;

@Service
@Transactional
public class CreateTable2 {
	private static AnnotationConfigApplicationContext ctx = Spring.context;
	@PersistenceContext
	protected EntityManager entityManager;
	
	public void init() {
		entityManager.persist(user_create_ordinary);
		entityManager.persist(user_create_special);
		entityManager.persist(user_enable);
		entityManager.persist(user_disable);
		entityManager.persist(user_read_all);
		entityManager.persist(user_read_self);
		entityManager.persist(user_update_all);
		entityManager.persist(user_update_self);
		entityManager.persist(user_delete);
		
		entityManager.persist(admin);
		entityManager.persist(manager);
		entityManager.persist(employee);
		entityManager.persist(user);
		
		entityManager.persist(company);
		entityManager.persist(product);
		entityManager.persist(qa);
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
