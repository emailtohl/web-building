package com.github.emailtohl.building.site.service.impl;
import static com.github.emailtohl.building.initdb.PersistenceData.admin;
import static com.github.emailtohl.building.initdb.PersistenceData.employee;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.Validator;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.dto.UserDto.UserType;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class UserServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject UserService userService;
	@Inject AuthenticationService authenticationService;
	UserDto u;
	
	@Before
	public void setUp() {
		u = new UserDto();
		u.setAddress("四川路");
		u.setAge(20);
		u.setRoles(new HashSet<Role>(Arrays.asList(employee, admin)));
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
		Long id = userService.addUser(u);
		try {
			assertNotNull(id);
			// test query
			UserDto qu = userService.getUser(id);
			assertEquals(u.getEmail(), qu.getEmail());
			// test update
			UserDto uu = new UserDto();
			uu.setRoles(null);
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
			String em = "test@test.com", pw = "987654321";
			userService.changePassword(em, pw);
			qu = userService.getUser(id);
			Authentication a = authenticationService.authenticate(em, pw);
			assertNotNull(a);
		} catch (Exception e) {
			throw e;
		} finally {
			userService.deleteUser(id);
		}
	}

	@Test
	public void testAddUser() {
		u.setPost("职位");
		Long id = userService.addUser(u);
		UserDto dto = userService.getUser(id);
		assertNull(dto.getPost());
		userService.deleteUser(id);
		
		u.setUserType(UserType.EMPLOYEE);
		id = userService.addUser(u);
		dto = userService.getUser(id);
		assertEquals("职位", dto.getPost());
		userService.deleteUser(id);
		
		u.setUserType(UserType.MANAGER);
		id = userService.addUser(u);
		dto = userService.getUser(id);
		assertEquals("职位", dto.getPost());
		userService.deleteUser(id);
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
