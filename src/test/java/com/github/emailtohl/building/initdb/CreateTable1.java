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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
public class CreateTable1 {

	public void init() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("building-unit");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
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

		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	public static void main(String[] args) throws Exception {
		CreateTable1 ct = new CreateTable1();
		ct.init();
	}

}
