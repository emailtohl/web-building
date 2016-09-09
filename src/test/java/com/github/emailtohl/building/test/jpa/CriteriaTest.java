package com.github.emailtohl.building.test.jpa;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.jpa.AbstractJpaRepository;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;

public class CriteriaTest {
	Concrete concrete;
	String jpql = "select u from User u join u.authorities a where u.email = 'emailtohl@163.com' and a in ('ADMIN')";
	class Concrete extends AbstractJpaRepository<User, Long> {
		@SuppressWarnings("unchecked")
		List<User> searchByJpql() {
			return entityManager.createQuery(jpql).getResultList();
		}
		
		List<User> searchByCriteria() {
			CriteriaBuilder b = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> q = b.createQuery(User.class);
			Root<User> root = q.from(User.class);
//			Join<User, Authority> join = root.join("authorities");
			q.select(root).where(
				b.equal(root.get("email"), "emailtohl@163.com"),
				b.isMember(Authority.ADMIN, root.<Set<Authority>>get("authorities"))
			);
			return entityManager.createQuery(q).getResultList();
		}
	}
	
	@Before
	public void setUp() {
		concrete = new Concrete();
		AutowireCapableBeanFactory factory = SpringUtils.context.getAutowireCapableBeanFactory();
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
