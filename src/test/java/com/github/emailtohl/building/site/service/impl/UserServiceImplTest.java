package com.github.emailtohl.building.site.service.impl;
import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.employee;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.manager;
import static com.github.emailtohl.building.initdb.PersistenceData.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.Validator;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
@Transactional
public class UserServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject @Named("userServiceImpl") UserService userService;
	@Inject AuthenticationService authenticationService;
	@Inject SecurityContextManager securityContextManager;
	@Inject RoleRepository roleRepository;
	Employee emp;
	Customer cus;
	
	@Before
	public void setUp() {
		securityContextManager.setEmailtohl();
		
		emp = new Employee();
		emp.setAddress("四川路");
		emp.setAge(20);
		emp.getRoles().addAll(Arrays.asList(employee, manager));
		employee.getUsers().add(emp);
		manager.getUsers().add(emp);
		emp.setBirthday(Date.from(Instant.now().minus(Duration.ofDays(10000))));
		emp.setDescription("test");
		emp.setEmail("testEmp@test.com");
		emp.setPassword("1234567890");
		emp.setName("name");
		emp.setTelephone("123456789");
		emp.setUsername("username");
		emp.setGender(Gender.MALE);
		Subsidiary ec = new Subsidiary();
		ec.setCity("成都");
		ec.setCountry("中国");
		ec.setLanguage("zh");
		ec.setProvince("四川");
		emp.setSubsidiary(ec);
		Set<ConstraintViolation<User>> set = Validator.validate(emp);
		logger.debug(set);
		
		cus = new Customer();
		cus.setAddress("四川路");
		cus.setAge(20);
		cus.getRoles().add(user);
		user.getUsers().add(cus);
		cus.setBirthday(Date.from(Instant.now().minus(Duration.ofDays(10000))));
		cus.setDescription("test");
		cus.setEmail("testCus@test.com");
		cus.setPassword("1234567890");
		cus.setName("name");
		cus.setTelephone("123456789");
		cus.setUsername("username");
		cus.setGender(Gender.FEMALE);
		Subsidiary cc = new Subsidiary();
		cc.setCity("成都");
		cc.setCountry("中国");
		cc.setLanguage("zh");
		cc.setProvince("四川");
		cus.setSubsidiary(cc);
		set = Validator.validate(cus);
		logger.debug(set);
	}
	
	@Test
	public void testCRUD1() {
		Long id = userService.addEmployee(emp);
		try {
			assertNotNull(id);
			// test query
			User qu = userService.getUser(id);
			assertTrue(qu instanceof Employee);
			assertEquals(emp.getEmail(), qu.getEmail());
			// test update
			Employee uu = new Employee();
			uu.setRoles(null);
			uu.setDescription("已修改");
			userService.mergeEmployee(emp.getEmail(), uu);
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
			// test grantRoles
			userService.grantRoles(id, Role.EMPLOYEE, Role.MANAGER);
			qu = userService.getUser(id);
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.EMPLOYEE)));
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.MANAGER)));
			
			userService.grantUserRole(id);
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.USER)));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			userService.deleteUser(id);
		}
	}
	
	@Test
	public void testCRUD2() {
		Long id = userService.addCustomer(cus);
		try {
			assertNotNull(id);
			// test query
			User qu = userService.getUser(id);
			assertTrue(qu instanceof Customer);
			assertEquals(cus.getEmail(), qu.getEmail());
			// test update
			Customer uu = new Customer();
			uu.setRoles(null);
			uu.setDescription("已修改");
			userService.mergeCustomer(cus.getEmail(), uu);
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
			
			userService.grantUserRole(id);
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.USER)));
			// test grantRoles
			userService.grantRoles(id, Role.EMPLOYEE, Role.MANAGER);
			qu = userService.getUser(id);
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.EMPLOYEE)));
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.MANAGER)));
			assertFalse(qu.getRoles().contains(roleRepository.findByName(Role.USER)));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			userService.deleteUser(id);
		}
	}

	@Test
	public void testChangePassword() {
		securityContextManager.setBar();
		String old = "123456";
		String pw = "987654321";
		userService.changePassword(bar.getEmail(), pw);
		Authentication a = authenticationService.authenticate(bar.getEmail(), pw);
		assertNotNull(a);
		assertEquals(bar.getUsername(), a.getName());
		userService.changePassword(bar.getEmail(), old);
	}
	
	@Test
	public void testGetUserPager() {
		// 查询页从第0页开始
		User u = new User();
		BeanUtils.copyProperties(foo, u, "iconSrc", "icon", "password");
		Pager<User> p = userService.getUserPager(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
	@Test
	public void testGetUserPage() {
		// 查询页从第0页开始
		User u = new User();
		BeanUtils.copyProperties(foo, u, "iconSrc", "icon", "password", "accountNonExpired");
		Page<User> p = userService.getUserPage(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
	@Test
	public void testAuthenticate() {
		userService.authenticate(foo.getEmail(), foo.getPassword());
	}
}
