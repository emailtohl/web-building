package com.github.emailtohl.building.test.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * 试验性代码的测试
 * @author HeLei
 */
public class RepositoryTest {
	static EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;
	
	@BeforeClass
	public static void beforeClass() {
		entityManagerFactory = Persistence.createEntityManagerFactory("building-unit");
	}
	
	@Before
	public void setUp() {
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
	}
	
	@After
	public void tearDown() {
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@AfterClass
	public static void afterClass() {
		entityManagerFactory.close();
	}
	
	/*@Test
	public void test01() {
		String jpql = "SELECT h FROM ApplicationHandleHistory h WHERE h.applicationForm.id = ?1";
		List<Object> ls = entityManager.createQuery(jpql).setParameter(1, 3375L).getResultList();
		for (Object o : ls) {
			System.out.println(o);
		}
	}*/
}
