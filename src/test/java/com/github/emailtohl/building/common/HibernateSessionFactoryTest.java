package com.github.emailtohl.building.common;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_DEVELPMENT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.config.RootContextConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
@ActiveProfiles({ PROFILE_DEVELPMENT })
public class HibernateSessionFactoryTest {

	@Inject
	HibernateSessionFactory f;
	
	@Test
	public void testGetSession() {
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
