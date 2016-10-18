package com.github.emailtohl.building.test.jpa;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.common.jpa.AbstractJpaRepository;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;

public class CriteriaTest {
	Concrete concrete;
	String jpql = "select u from User u join u.roles r where u.email = 'emailtohl@163.com' and r.name in ('" + Role.ADMIN + "')";
	
	class Concrete extends AbstractJpaRepository<User, Long> {
		@SuppressWarnings("unchecked")
		List<User> searchByJpql() {
			return entityManager.createQuery(jpql).getResultList();
		}
		
		List<User> searchByCriteria() {
			CriteriaBuilder b = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> q = b.createQuery(User.class);
			Root<User> root = q.from(User.class);
			Join<User, Role> join = root.join("roles");
			q.select(root).where(
				b.equal(root.get("email"), "emailtohl@163.com"),
				join.get("name").in(Arrays.asList(Role.ADMIN))
			);
			return entityManager.createQuery(q).getResultList();
		}
	}
	
	@Before
	public void setUp() {
		concrete = new Concrete();
		AutowireCapableBeanFactory factory = Spring.context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(concrete, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(concrete, "criteriaTest");
	}
	
	@Test
	public void testSearch() {
		List<User> ls1 = concrete.searchByJpql();
		System.out.println(ls1);
		List<User> ls2 = concrete.searchByCriteria();
		System.out.println(ls2);
		assertTrue(ls1.get(0).equals(ls2.get(0)));
	}
}
