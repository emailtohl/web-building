package com.github.emailtohl.building.common.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

public final class Validator {
	public final static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	public final static javax.validation.Validator validator = factory.getValidator();
	
	public static <T> Set<ConstraintViolation<T>> validate(T bean) {
		return validator.validate(bean);
	}
}
