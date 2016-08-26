package com.github.emailtohl.building.site.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.TransactionSystemException;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.common.utils.BCryptUtil;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;

public class UserRepositoryTest {
	AnnotationConfigApplicationContext ctx = SpringUtils.ctx;
	UserRepository userRepository = ctx.getBean(UserRepository.class);
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
	BCryptUtil bCryptUtil = ctx.getBean(BCryptUtil.class);
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
		User u = new Manager();
		u.setEmail("test@test.com");
		u.setPassword(bCryptUtil.hash("123456"));
		userRepository.saveAndFlush(u);
		Long id = u.getId();
		assertNotNull(id);
//		User uu = userRepository.findOne(id);
		User uu = userRepository.get(id);
		assertEquals(u, uu);
		userRepository.delete(id);
		assertFalse(userRepository.exists(id));
	}

	/**
	 * 测试动态查询
	 */
	@Test
	public void testDynamicQuery() {
		Pager<User> p = userRepository.dynamicQuery(PersistenceData.foo, 1L);
		System.out.println(p.getDataList());
		assertNotNull(p.getDataList());
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
