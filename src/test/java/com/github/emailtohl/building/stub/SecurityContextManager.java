package com.github.emailtohl.building.stub;

import static com.github.emailtohl.building.initdb.PersistenceData.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 依赖于AuthenticationManager
 * 为测试环境提供已认证的用户
 * 
 * @author HeLei
 */
public class SecurityContextManager {
	private static final Logger logger = LogManager.getLogger();
	private AuthenticationManager authenticationManager;
	private final String password = "123456";
	
	public SecurityContextManager(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}
	
	public void clearContext() {
		SecurityContextHolder.clearContext();
	}
	
	public void setEmailtohl() {
		SecurityContextHolder.clearContext();
		String name = emailtohl.getEmail();
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		logger.debug(authentication.getPrincipal());
	}
	
	public void setFoo() {
		SecurityContextHolder.clearContext();
		String name = foo.getEmail();
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		logger.debug(authentication.getPrincipal());
	}
	
	public void setBar() {
		SecurityContextHolder.clearContext();
		String name = bar.getEmail();
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		logger.debug(authentication.getPrincipal());
	}
	
	public void setBaz() {
		SecurityContextHolder.clearContext();
		String name = baz.getEmail();
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		logger.debug(authentication.getPrincipal());
	}
}
