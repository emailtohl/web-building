package com.github.emailtohl.building.site.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

public class UserTest {

	@Test
	public void testGetAge() throws ParseException {
		SimpleDateFormat s = new SimpleDateFormat("YYYY-MM-DD");
		User u = new User();
		u.setBirthday(s.parse("1982-02-12"));
		System.out.println(u.getAge());
	}

}
