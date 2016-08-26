package com.github.emailtohl.building.site.service;

import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.site.entities.User;

/**
 * 认证服务
 * 
 * @author Helei
 */
@Validated
public interface AuthenticationService {
	/**
	 * 如果通过认证，则返回该账号的User对象，否则返回null
	 * @param email
	 * @param password
	 * @return null为认证失败
	 */
	User authenticate(String email, String password);
}
