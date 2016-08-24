package com.github.emailtohl.building.site.dao;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.entities.User;

public class UserRepositoryTest {
	AnnotationConfigApplicationContext ctx = SpringUtils.ctx;
	UserRepository userRepository = ctx.getBean(UserRepository.class);
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
	
	@Test
	public void testFindByEmail() {
		User user = userRepository.findByEmail("emailtohl@163.com");
		Assert.assertEquals("emailtohl@163.com", user.getEmail());
	}

	@Test
	public void testFindByBirthdayBetween() throws ParseException {
		Date begin = format.parse("1982-01-01");
		Date end = format.parse("1983-01-01");
		List<User> ls = userRepository.findByBirthdayBetween(begin, end);
		Assert.assertFalse(ls.isEmpty());
	}

	@Test
	public void testSaveIterableOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveS() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneID() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteT() {
		fail("Not yet implemented");
	}

}
