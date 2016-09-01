package com.github.emailtohl.building.site.service;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.site.entities.Authority;

/**
 * 认证和授权服务
 * 
 * @author Helei
 */
@Validated
@Transactional
public interface AuthenticationService extends AuthenticationProvider {
	/**
	 * 认证用户，由于要返回用户信息，所以只有本人才能调用。
	 * 事实上认证功能已经交给spring security框架完成
	 * @param email
	 * @param password
	 * @return Authentication 代表用户身份的对象
	 */
	Authentication authenticate(String email, String password);
	
	/**
	 * 为用户授权
	 * 授权的逻辑较为复杂，需要在实现类中细化
	 * 例如MANAGER不能为其他人分配ADMIN
	 * 
	 * @param id
	 * @param authorities
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	void grantedAuthority(@Min(value = 1L) Long id, @NotNull Set<Authority> authorities);
}
