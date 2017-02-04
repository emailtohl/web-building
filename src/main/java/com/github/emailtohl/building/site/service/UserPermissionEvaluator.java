package com.github.emailtohl.building.site.service;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
/**
 * 许可计算器，实现spring security的PermissionEvaluator接口，可在注解@PreFilter和@PostFilter中使用，例如：
 * @PreFilter("hasPermission(targetObject, 'delete')")
 * public void delete(List<User> users) { ... }
 * 这里的targetObject是spring security提供的对切点方法参数的引用，在这里它就是users
 * 
 * 对应在实现了PermissionEvaluator的接口中
 * public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
 * 第二个参数targetDomainObject就是users，第三个参数permission就是'delete'
 * 
 * @author HeLei
 * @date 2017.02.04
 */
public class UserPermissionEvaluator implements PermissionEvaluator {
	private static final Logger logger = LogManager.getLogger();
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		logger.info(authentication.getAuthorities());
		logger.info(targetDomainObject);
		logger.info(permission);
		boolean flag = true;
		return flag;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		logger.info(authentication.getAuthorities());
		logger.info(targetId);
		logger.info(targetType);
		logger.info(permission);
		boolean flag = true;
		return flag;
	}

}
