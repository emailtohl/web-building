package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.company;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.product;
import static com.github.emailtohl.building.initdb.PersistenceData.qa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.bootspring.SpringUtils;

@Service
@Transactional
public class CreateTable2 {
	private static AnnotationConfigApplicationContext ctx = SpringUtils.context;
	@PersistenceContext
	protected EntityManager entityManager;
	
	public void init() {
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
