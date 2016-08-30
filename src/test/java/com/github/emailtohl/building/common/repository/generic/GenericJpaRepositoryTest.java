package com.github.emailtohl.building.common.repository.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;

public class GenericJpaRepositoryTest {
	@SuppressWarnings("unchecked")
	GenericRepository<Long, User> dao = (GenericRepository<Long, User>) SpringUtils.context.getBean("jpaDao");
	User u = new User();
	
	@Before
	public void setUp() {
		u.setAddress("四川路");
		u.setAge(20);
		u.setAuthorities(new HashSet<Authority>(Arrays.asList(Authority.EMPLOYEE, Authority.ADMIN)));
		u.setBirthday(Date.from(Instant.now().minus(Duration.ofDays(10000))));
		u.setDescription("test");
		u.setEmail("test@test.com");
		u.setPassword("1234567890");
		u.setEnabled(true);
		u.setName("name");
		u.setTelephone("123456789");
		u.setUsername("username");
		u.setGender(Gender.MALE);
		Subsidiary c = new Subsidiary();
		c.setCity("成都");
		c.setCountry("中国");
		c.setLanguage("zh");
		c.setProvince("四川");
	}
	
	@Test
	public void crud() {
		// test add
		dao.add(u);
		Long id = u.getId();
		assertNotNull(id);
		// test query
		User qu = dao.get(id);
		assertEquals(u, qu);
		// test update
		qu.setDescription("已修改");
		qu.setAuthorities(null);
		dao.update(qu);
		qu = dao.get(id);
		assertEquals("已修改", qu.getDescription());
		dao.removeById(id);
		assertNull(dao.get(id));
	}
	
}
