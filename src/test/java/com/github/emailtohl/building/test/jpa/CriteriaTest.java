package com.github.emailtohl.building.test.jpa;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.AbstractJpaRepository;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.entities.user.User;
/**
 * JPA标准查询的测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class CriteriaTest {
	@Inject ApplicationContext context;
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
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
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
