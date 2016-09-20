package com.github.emailtohl.building.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VerifyFailure extends RuntimeException {
	private static final long serialVersionUID = 9186066996519484932L;

	public VerifyFailure() {
		super();
	}

	public VerifyFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public VerifyFailure(String message, Throwable cause) {
		super(message, cause);
	}

	public VerifyFailure(String message) {
		super(message);
	}

	public VerifyFailure(Throwable cause) {
		super(cause);
	}
	
}
