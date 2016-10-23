package com.github.emailtohl.building.common.utils;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.User;

public class ValidatorTest {

	@Test
	public void testValidate() {
		User u = new Employee();
		u.setEmail("test");
		Set<ConstraintViolation<User>> violations = Validator.validate(u);
		System.out.println(violations);
		assertTrue(violations.size() > 0);
	}

}
