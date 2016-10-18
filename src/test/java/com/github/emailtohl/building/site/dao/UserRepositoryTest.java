package com.github.emailtohl.building.site.dao;

import static com.github.emailtohl.building.initdb.PersistenceData.manager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.TransactionSystemException;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.Validator;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;

public class UserRepositoryTest {
	static final Logger logger = LogManager.getLogger();
	AnnotationConfigApplicationContext ctx = Spring.context;
	UserRepository userRepository = ctx.getBean(UserRepository.class);
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
	
	/**
	 * 测试spring data中的命名方法
	 */
	@Test
	public void testFindByEmail() {
		User user = userRepository.findByEmail("emailtohl@163.com");
		assertEquals("emailtohl@163.com", user.getEmail());
	}
	/**
	 * 测试spring data中的命名方法
	 */
	@Test
	public void testFindByBirthdayBetween() throws ParseException {
		Date begin = format.parse("1982-01-01");
		Date end = format.parse("1983-01-01");
		List<User> ls = userRepository.findByBirthdayBetween(begin, end);
		assertFalse(ls.isEmpty());
	}

	/**
	 * 测试增删改查
	 */
	@Test
	public void testCRUD() {
		Role employee, admin;
		RoleRepository roleRepository = Spring.context.getBean(RoleRepository.class);
		employee = roleRepository.findByName(Role.EMPLOYEE);
		admin = roleRepository.findByName(Role.ADMIN);
		
		User u = new Manager();
		u.setAddress("四川路");
		u.setAge(20);
		u.setRoles(new HashSet<Role>(Arrays.asList(employee, admin)));
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
		Set<ConstraintViolation<User>> set = Validator.validate(u);
		logger.debug(set);
		// test add
		userRepository.save(u);
		Long id = u.getId();
		try {
			assertNotNull(id);
			// test query
			User qu = userRepository.findOne(id);
			assertEquals(u, qu);
			// test update
			qu.setDescription("已修改");
			userRepository.save(qu);
			userRepository.flush();
			qu = userRepository.findOne(id);
			assertEquals("已修改", qu.getDescription());
		} catch (Exception e) {
			throw e;
		} finally {
			userRepository.delete(id);
		}
		assertFalse(userRepository.exists(id));
	}

	/**
	 * 测试动态查询
	 */
	@Test
	public void testDynamicQuery() {
		Pager<User> p = userRepository.dynamicQuery(PersistenceData.foo, 1);
		System.out.println(p.getContent());
		assertNotNull(p.getContent());
	}
	
	/**
	 * 将自定义的Pager转换成Spring data的Page
	 * 不过Page的有些方法返回数据不正确，例如getNumberOfElements、getTotalElements、hasPrevious等
	 * 不过若只需确定的getContent和当前页getNumber没有问题
	 */
	@Test
	public void testGetPage() {
		PageRequest pageable = new PageRequest(0, 20);
		User u = new User();
		u.setEmail("foo@test.com");
		Subsidiary s = new Subsidiary();
		s.setCity("西安");
		u.setSubsidiary(s);
		u.setRoles(new HashSet<Role>(Arrays.asList(manager)));
		Page<User> p = userRepository.getPage(u, pageable);
		logger.debug("getNumber:" + p.getNumber());
		logger.debug("getNumberOfElements:" + p.getNumberOfElements());
		logger.debug("getSize:" + p.getSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.hasContent());
		logger.debug("hasPrevious:" + p.hasPrevious());
		logger.debug("isFirst:" + p.isFirst());
		logger.debug("isLast:" + p.isLast());
		logger.debug("getContent:" + p.getContent());
		logger.debug("getSort:" + p.getSort());
		logger.debug("nextPageable:" + p.nextPageable());
		logger.debug("previousPageable:" + p.previousPageable());
		logger.debug("spliterator:" + p.spliterator());
	}
	
	@Test
	public void testGetMaxEmpNo() {
		logger.debug(userRepository.getMaxEmpNo());
		assertNotNull(userRepository.getMaxEmpNo());
	}
	
	@Test
	public void testGetPagerByRoles() {
		PageRequest pageable = new PageRequest(0, 20);
		User u = new User();
		u.setEmail("foo@test.com");
		Subsidiary s = new Subsidiary();
		s.setCity("西安");
		u.setSubsidiary(s);
		u.setRoles(new HashSet<Role>(Arrays.asList(manager)));
		Pager<User> p = userRepository.getPagerByRoles(u, pageable);
		logger.debug("getNumber:" + p.getPageNumber());
		logger.debug("getNumberOfElements:" + p.getTotalElements());
		logger.debug("getSize:" + p.getPageSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.getContent());
		assertEquals(0, p.getPageNumber());
		assertEquals(1, p.getTotalElements());
		assertEquals(1, p.getTotalPages());
	}
	
	@Test
	public void testGetPagerByCriteria() {
		PageRequest pageable = new PageRequest(0, 20);
		User u = new User();
		u.setEmail("foo@test.com");
		Subsidiary s = new Subsidiary();
		s.setCity("西安");
		u.setSubsidiary(s);
		u.setRoles(new HashSet<Role>(Arrays.asList(manager)));
		Pager<User> p = userRepository.getPagerByCriteria(u, pageable);
		System.out.println(p);
		logger.debug("getNumber:" + p.getPageNumber());
		logger.debug("getNumberOfElements:" + p.getTotalElements());
		logger.debug("getSize:" + p.getPageSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.getContent());
		assertEquals(1, p.getTotalElements());
		
		u.setEmail(null);
		u.getRoles().clear();
		p = userRepository.getPagerByCriteria(u, pageable);
		logger.debug("getNumber:" + p.getPageNumber());
		logger.debug("getNumberOfElements:" + p.getTotalElements());
		logger.debug("getSize:" + p.getPageSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.getContent());
		assertEquals(3, p.getTotalElements());
	}
	
	/**
	 * 测试需要校验的
	 */
	@Test(expected = TransactionSystemException.class)
	public void testNotNull() {
		User u = new Manager();
		userRepository.save(u);
	}
}
