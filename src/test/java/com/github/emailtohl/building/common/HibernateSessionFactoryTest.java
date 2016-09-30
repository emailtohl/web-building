package com.github.emailtohl.building.common;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Session;
import org.junit.Test;

import com.github.emailtohl.building.bootspring.SpringUtils;

public class HibernateSessionFactoryTest {

	@Test
	public void testGetSession() {
		HibernateSessionFactory f = SpringUtils.context.getBean(HibernateSessionFactory.class);
		Session s = f.getSession();
		s.beginTransaction();
		@SuppressWarnings("rawtypes")
		List l = s.createQuery("select u from User u where u.email = ?1")
		.setParameter(1, "foo@test.com").getResultList();
		for (Object o : l) {
			System.out.println(o);
		}
		assertFalse(l.isEmpty());
		s.close();
		
		s = f.getSession();
		assertTrue(s.isOpen());
		f.closeSession();
	}

}
