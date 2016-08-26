package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.company;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.product;
import static com.github.emailtohl.building.initdb.PersistenceData.qa;

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
