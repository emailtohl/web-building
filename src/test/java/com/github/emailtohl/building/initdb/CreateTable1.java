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
import static com.github.emailtohl.building.initdb.PersistenceData.user_update_self;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
public class CreateTable1 {

	public void init() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("building-unit");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		entityManager.persist(user_create_ordinary);
		entityManager.persist(user_create_special);
		entityManager.persist(user_enable);
		entityManager.persist(user_disable);
		entityManager.persist(user_read_all);
		entityManager.persist(user_read_self);
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

		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	public static void main(String[] args) throws Exception {
		CreateTable1 ct = new CreateTable1();
		ct.init();
	}

}
