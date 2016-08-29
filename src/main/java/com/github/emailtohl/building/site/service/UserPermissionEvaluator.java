package com.github.emailtohl.building.site.service;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
/**
 * 许可计算器，实现spring security的PermissionEvaluator接口，可在注解中使用，例如：
 * @PreAuthorize("hasPermission(filterObject, 'ADMIN')")
 * @author Helei
 */
public class UserPermissionEvaluator implements PermissionEvaluator {
	private static final Logger logger = LogManager.getLogger();
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		logger.info(authentication.getAuthorities());
		logger.info(targetDomainObject);
		logger.info(permission);
		return true;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

}
