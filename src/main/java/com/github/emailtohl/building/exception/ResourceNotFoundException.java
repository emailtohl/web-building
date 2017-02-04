package com.github.emailtohl.building.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * 注解了@ResponseStatus的异常被抛出后，Spring MVC会自动根据其状态自动调用HttpServletResponse.setStatus()方法
 * @author HeLei
 * @date 2017.02.04
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RestException {
	private static final long serialVersionUID = -1017479456824427572L;

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

}
