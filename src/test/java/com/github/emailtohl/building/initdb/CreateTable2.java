package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.bootspring.Spring;

@Service
@Transactional
public class CreateTable2 {
	@PersistenceContext
	protected EntityManager entityManager;
	
	public void init() {
		entityManager.persist(user_role_authority_allocation);
		entityManager.persist(user_create_ordinary);
		entityManager.persist(user_create_special);
		entityManager.persist(user_enable);
		entityManager.persist(user_disable);
		entityManager.persist(user_grant_roles);
		entityManager.persist(user_read_all);
		entityManager.persist(user_read_self);
		entityManager.persist(user_update_all);
		entityManager.persist(user_update_self);
		entityManager.persist(user_delete);
		entityManager.persist(application_form_transit);
		entityManager.persist(application_form_read_history);
		entityManager.persist(application_form_delete);
		entityManager.persist(forum_delete);
		
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
		entityManager.persist(baz);
		entityManager.persist(qux);
	}
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext ctx = Spring.getApplicationContext();
		CreateTable2 ct = ctx.getBean(CreateTable2.class);
		ct.init();
		System.exit(0);
	}

}
