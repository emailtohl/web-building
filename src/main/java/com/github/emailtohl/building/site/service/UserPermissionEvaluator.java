package com.github.emailtohl.building.site.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import static com.github.emailtohl.building.site.entities.Authority.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
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
		boolean flag = false;
		if (targetDomainObject instanceof User && permission instanceof String) {
			User u = (User) targetDomainObject;
			Collection<Authority> set = u.getAuthorities();
			switch ((String) permission) {
			case "addUser":
				if (set.contains(ADMIN)) {// 如果是管理员，则许可
					flag = true;
				} else if (set.contains(MANAGER)) {// 如果是经理，则不能将角色改为ADMIN
					if (!u.getAuthorities().contains(ADMIN)) {
						flag = true;
					}
				} else if (set.contains(EMPLOYEE)) {// 如果是员工，则不能将角色改为MANAGER或ADMIN，且不能启用账户
					if (!u.getAuthorities().contains(ADMIN) && !u.getAuthorities().contains(MANAGER)) {
						flag = true;
					}
				} else {
					if (!u.getAuthorities().contains(ADMIN)
							&& !u.getAuthorities().contains(MANAGER)
							&& !u.getAuthorities().contains(EMPLOYEE)
							&& (u.getEnabled() == null || !u.getEnabled())) {
						flag = true;
					}
				}
				break;
			case "updateUser":
				if (set.contains(ADMIN)) {// 如果是管理员，则许可
					flag = true;
				} else if (set.contains(MANAGER)) {// 如果是经理，则不能将角色改为ADMIN
					if (!u.getAuthorities().contains(ADMIN)) {
						flag = true;
					}
				} else if (set.contains(EMPLOYEE)) {// 如果是员工，则不能将角色改为MANAGER或ADMIN，且不能修改其他账号的
					if (!u.getAuthorities().contains(ADMIN) && !u.getAuthorities().contains(MANAGER)
							 ) {
						flag = true;
					}
				} else {
					if (!u.getAuthorities().contains(ADMIN)
							&& !u.getAuthorities().contains(MANAGER)
							&& !u.getAuthorities().contains(EMPLOYEE)
							&& (u.getEnabled() == null || !u.getEnabled())) {
						flag = true;
					}
				}
				break;
			case "DELETE_USER":
				
				break;
			}
		}
		return flag;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

}
