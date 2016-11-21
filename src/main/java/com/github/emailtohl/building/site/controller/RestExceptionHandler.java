package com.github.emailtohl.building.site.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * RestFul不应该返回错误页面，此控制器将返回RestFul风格的错误信息
 * 
 * @author HeLei
 */
@ControllerAdvice
public class RestExceptionHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		ErrorResponse errors = new ErrorResponse();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getMessageTemplate());
			error.setMessage(violation.getMessage());
			errors.addError(error);
		}
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
		ErrorResponse er = new ErrorResponse();
		ErrorItem i = new ErrorItem();
		i.setMessage(e.getMessage());
		er.addError(i);
		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
	}

	public static class ErrorItem {
		private String code;
		private String message;

		@XmlAttribute
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		@XmlValue
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	@XmlRootElement(name = "errors")
	public static class ErrorResponse {
		private List<ErrorItem> errors = new ArrayList<>();

		@XmlElement(name = "error")
		public List<ErrorItem> getErrors() {
			return errors;
		}

		public void setErrors(List<ErrorItem> errors) {
			this.errors = errors;
		}

		public void addError(ErrorItem error) {
			this.errors.add(error);
		}
	}

}
