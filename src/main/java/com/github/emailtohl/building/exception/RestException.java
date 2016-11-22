package com.github.emailtohl.building.exception;

/**
 * 为了在Spring MVC异常处理器中更好地识别出RESTful的异常，便于以RESTful风格返回错误信息（否则返回错误页面）
 * 继承RestException的异常均以RESTful风格返回错误信息
 * @author HeLei
 */
public class RestException extends RuntimeException {
	private static final long serialVersionUID = -3089217262069816787L;

	public RestException() {
		super();
	}

	public RestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RestException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestException(String message) {
		super(message);
	}

	public RestException(Throwable cause) {
		super(cause);
	}

}
