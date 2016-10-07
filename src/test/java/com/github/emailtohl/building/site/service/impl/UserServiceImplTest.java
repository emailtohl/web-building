package com.github.emailtohl.building.site.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.Validator;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;
import com.github.emailtohl.building.site.service.UserService;

public class UserServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	UserService userService;
	UserDto u;
	
	@Before
	public void setUp() {
		userService = SpringUtils.context.getBean(UserService.class);
		u = new UserDto();
		u.setAddress("四川路");
		u.setAge(20);
		u.setAuthorities(new HashSet<Authority>(Arrays.asList(Authority.EMPLOYEE, Authority.ADMIN)));
		u.setBirthday(Date.from(Instant.now().minus(Duration.ofDays(10000))));
		u.setDescription("test");
		u.setEmail("test@test.com");
		u.setPassword("1234567890");
		u.setName("name");
		u.setTelephone("123456789");
		u.setUsername("username");
		u.setGender(Gender.MALE);
		Subsidiary c = new Subsidiary();
		c.setCity("成都");
		c.setCountry("中国");
		c.setLanguage("zh");
		c.setProvince("四川");
		Set<ConstraintViolation<User>> set = Validator.validate(u);
		logger.debug(set);
	}
	
	@Test
	public void testCRUD() {
		userService.addUser(u);
		Long id = u.getId();
		try {
			assertNotNull(id);
			// test query
			UserDto qu = userService.getUser(id);
			assertEquals(u, qu);
			// test update
			UserDto uu = new UserDto();
			uu.setAuthorities(null);
			uu.setDescription("已修改");
			userService.mergeUser(id, uu);
			qu = userService.getUser(id);
			assertEquals("已修改", qu.getDescription());
			// test enable
			userService.enableUser(id);
			qu = userService.getUser(id);
			assertTrue(qu.getEnabled());
			// test disable
			userService.disableUser(id);
			qu = userService.getUser(id);
			assertFalse(qu.getEnabled());
			// test change password
			userService.changePassword("test@test.com", "987654321");
			qu = userService.getUser(id);
		} catch (Exception e) {
			throw e;
		} finally {
			userService.deleteUser(id);
		}
	}

	@Test
	public void testGetUserPager() {
		UserDto u = new UserDto();
		u.setEmail("foo@test.com");
		// 查询页从第0页开始
		Pager<UserDto> p = userService.getUserPager(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
	@Test
	public void testGetUserPage() {
		UserDto u = new UserDto();
		u.setEmail("foo@test.com");
		// 查询页从第0页开始
		Page<UserDto> p = userService.getUserPage(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
}
