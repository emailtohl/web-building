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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
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

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.Validator;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class UserServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject @Named("userServiceImpl") UserService userService;
	@Inject SecurityContextManager securityContextManager;
	@Inject RoleRepository roleRepository;
	@Inject Gson gson;
	@Inject CleanAuditData cleanAuditData;
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
		cus.setTitle("客户甲乙丙");
		cus.setAffiliation("某某科技公司");
		
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
			qu = userService.getUser(id);
			assertTrue(qu.getRoles().contains(roleRepository.findByName(Role.USER)));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			userService.deleteUser(id);
			cleanAuditData.cleanUserAudit(id);
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
			cleanAuditData.cleanUserAudit(id);
		}
	}

//	@Test
	public void testChangePassword() {
		securityContextManager.setBar();
		String old = "123456";
		String pw = "987654321";
		userService.changePassword(bar.getEmail(), pw);
		Authentication a = userService.authenticate(bar.getEmail(), pw);
		assertNotNull(a);
		assertEquals(bar.getName(), a.getName());
		userService.changePassword(bar.getEmail(), old);
	}
	
	public void testUpdateIcon() {
		String iconSrc = "img/icon-head-foo.jpg";
		securityContextManager.setFoo();
		ClassLoader cl = PersistenceData.class.getClassLoader();
		// cl.getResourceAsStream方法返回的输入流已经是BufferedInputStream对象，无需再装饰
		try (InputStream is = cl.getResourceAsStream(iconSrc)) {
			byte[] icon = new byte[is.available()];
			is.read(icon);
			long id = userService.getUserByEmail(foo.getEmail()).getId();
			userService.updateIconSrc(id, iconSrc);
			userService.updateIcon(id, icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetUserPager() {
		// 查询页从第0页开始
		User u = new User();
		u.setUsername(foo.getUsername());
		u.setRoles(foo.getRoles());
		u.setEmail(foo.getEmail());
		Pager<User> p = userService.getUserPager(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
	@Test
	public void testGetUserPage() {
		// 查询页从第0页开始
		User u = new User();
		u.setUsername(foo.getUsername());
		u.setRoles(foo.getRoles());
		u.setEmail(foo.getEmail());
		Page<User> p = userService.getUserPage(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
	@Test
	public void testAuthenticate() {
		Authentication a = userService.authenticate(foo.getEmail(), "123456");
		assertNotNull(a);
		// 查看认证结果
		logger.debug(gson.toJson(a));
	}
	
	@Test
	public void testPublicKey() {
		securityContextManager.setFoo();
		userService.setPublicKey(new BigInteger("100"), new BigInteger("200"));
		User u = userService.getUserByEmail(foo.getEmail());
		assertNotNull(u.getPublicKey());
		assertNotNull(u.getModule());
		userService.clearPublicKey();
	}

}
