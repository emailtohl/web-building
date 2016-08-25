package com.github.emailtohl.building.common.utils;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;

public class ValidatorTest {

	@Test
	public void testValidate() {
		User u = new Manager();
		u.setEmail("test");
		u.setEmail("test@test.com");
		Set<ConstraintViolation<User>> violations = Validator.validate(u);
		System.out.println(violations);
		assertTrue(violations.size() == 0);
	}

}
