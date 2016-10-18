package com.github.emailtohl.building.site.entities;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.UserRepository;

public class UserTest {

	@Test
	public void testGetAge() throws ParseException {
		SimpleDateFormat s = new SimpleDateFormat("YYYY-MM-DD");
		User u = new User();
		u.setBirthday(s.parse("1982-02-12"));
		System.out.println(u.getAge());
	}

	@Test
	public void testGetIcon() throws FileNotFoundException, IOException {
		ClassLoader cl = UserTest.class.getClassLoader();
		AnnotationConfigApplicationContext ctx = Spring.context;
		UserRepository userRepository = ctx.getBean(UserRepository.class);
		User u = userRepository.findByEmail("emailtohl@163.com");
		try (InputStream is = cl.getResourceAsStream("img/icon-head-emailtohl.png")) {
			byte[] expecteds = new byte[is.available()];
			is.read(expecteds);
			byte[] actuals = u.getIcon();
			Assert.assertArrayEquals(expecteds, actuals);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAuthorities() {
		assertTrue(PersistenceData.emailtohl.authorities().contains(Authority.USER_DELETE));
	}
}
