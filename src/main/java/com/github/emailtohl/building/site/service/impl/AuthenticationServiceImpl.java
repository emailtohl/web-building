package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.site.entities.Authority.ADMIN;
import static com.github.emailtohl.building.site.entities.Authority.MANAGER;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;

/**
 * 认证服务的实现类
 * 
 * @author Helei
 */
@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger logger = LogManager.getLogger();
	@Inject UserRepository userRepository;

	@Override
	public Authentication authenticate(String email, String password) {
		User u = userRepository.findByEmail(email);
		if (u == null) {
			logger.warn("Authentication failed for non-existent user {}.", email);
			return null;
		}
		if (!BCrypt.checkpw(password, u.getPassword())) {
			logger.warn("Authentication failed for user {}.", email);
			return null;
		}
		logger.debug("User {} successfully authenticated.", email);
		return new Authentication() {
			private static final long serialVersionUID = -3836064005715328650L;
			
			@Override
			public String getName() {
				return u.getEmail();
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return AuthorityUtils.createAuthorityList(Authority.toStringArray(u.getAuthorities()));
			}

			@Override
			public Object getCredentials() {
				// 认证的时候存储密码，用过之后会擦除，所以直接返回null
				return null;
			}

			@Override
			public Object getDetails() {
				/*
				 * Stores additional details about the authentication request.
				 * These might be an IP address, certificate serial number etc.
				 */
				return u.getId();
			}

			@Override
			public Object getPrincipal() {
				/*
				 * The identity of the principal being authenticated. In the
				 * case of an authentication request with username and password,
				 * this would be the username. Callers are expected to populate
				 * the principal for an authentication request.
				 */
				return u.getEmail();
			}

			private boolean isAuthenticated = true;
			@Override
			public boolean isAuthenticated() {
				/*
				 * Used to indicate to AbstractSecurityInterceptor whether it
				 * should present the authentication token to the
				 * AuthenticationManager
				 * 指示AbstractSecurityInterceptor已被认证，所以返回true
				 */
				return isAuthenticated;
			}

			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
				this.isAuthenticated = isAuthenticated;
			}
		};
	}

	@Override
	public void grantedAuthority(Long id, Set<Authority> authorities) {
		Set<String> roles = getGrantedAuthoritySet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		if (!roles.contains("ADMIN") && authorities.contains(ADMIN)) {
			throw new AccessDeniedException("不能添加管理员权限");
		}
		if (!roles.contains("ADMIN") && !roles.contains("MANAGER") && (authorities.contains(MANAGER))) {
			throw new AccessDeniedException("没有权限添加MANAGER权限");
		}
		userRepository.findOne(id).setAuthorities(authorities);
	}
	
	private Set<String> getGrantedAuthoritySet(Collection<? extends GrantedAuthority> collection) {
		Set<String> set = new HashSet<String>();
		for (GrantedAuthority g : collection) {
			set.add(g.getAuthority());
		}
		return set;
	}
}
