package com.github.emailtohl.building.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VerifyFailure extends RuntimeException {
	private static final long serialVersionUID = 9186066996519484932L;
}
