package com.github.emailtohl.building.common.utils;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

import com.github.emailtohl.building.site.entities.user.Employee;
import com.github.emailtohl.building.site.entities.user.User;
/**
 * 校验器工具的测试
 * @author HeLei
 * @date 2017.02.04
 */
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
