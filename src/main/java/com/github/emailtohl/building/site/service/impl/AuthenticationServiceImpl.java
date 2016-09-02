package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.site.entities.Authority.ADMIN;
import static com.github.emailtohl.building.site.entities.Authority.MANAGER;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;

/**
 * 认证服务的实现类
 * 主要是为了实践自定义的AuthenticationProvider，用于spring security的配置中
 * @author Helei
 */
@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {
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
				 * 按照描述，getPrincipal()返回的应该是某种形式的用户名
				 * 但是spring security需要在这个返回中获取更多的用户信息，所以创建了一个实体类
				 */
				class Principal {
					Long id;
					String username;
					Set<GrantedAuthority> authorities;
					boolean enabled;
					@SuppressWarnings("unused")
					public Long getId() {
						return id;
					}
					public void setId(Long id) {
						this.id = id;
					}
					@SuppressWarnings("unused")
					public String getUsername() {
						return username;
					}
					public void setUsername(String username) {
						this.username = username;
					}
					@SuppressWarnings("unused")
					public Set<GrantedAuthority> getAuthorities() {
						return authorities;
					}
					public void setAuthorities(Set<GrantedAuthority> authorities) {
						this.authorities = authorities;
					}
					@SuppressWarnings("unused")
					public boolean isEnabled() {
						return enabled;
					}
					public void setEnabled(boolean enabled) {
						this.enabled = enabled;
					}
					@Override
					public String toString() {
						return "Principal [id=" + id + ", username=" + username + ", authorities=" + authorities
								+ ", enabled=" + enabled + "]";
					}
				}
				Principal p = new Principal();
				p.setId(u.getId());
				p.setUsername(u.getEmail());
				p.setEnabled(u.getEnabled());
				String[] roles = Authority.toStringArray(u.getAuthorities());
				List<GrantedAuthority> ls = AuthorityUtils.createAuthorityList(roles);
				p.setAuthorities(new HashSet<GrantedAuthority>(ls));
				return p;
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

	/**
	 * 实现AuthenticationProvider
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken credentials = (UsernamePasswordAuthenticationToken) authentication;
		String email = credentials.getPrincipal().toString();
		String password = credentials.getCredentials().toString();
		// 用户名和密码用完后，记得擦除
		credentials.eraseCredentials();
		return this.authenticate(email, password);
	}

	/**
	 * 实现AuthenticationProvider
	 * 保证传入public Authentication authenticate(Authentication authentication) throws AuthenticationException
	 * 方法中的认证模型一定是UsernamePasswordAuthenticationToken
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == UsernamePasswordAuthenticationToken.class;
	}

	/**
	 * 实现UserDetailsService
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	private Pattern p = Pattern.compile("username=([a-z0-9`!#$%^&*'{}?/+=|_~-]+(\\.[a-z0-9`!#$%^&*'{}?/+=|_~-]+)*@([a-z0-9]([a-z0-9-]*[a-z0-9])?)+(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*)");
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Matcher m = p.matcher(username);
		if (!m.find()) {
			return null;
		}
		String email = m.group(1);
		final User u = userRepository.findByEmail(email);
		return new UserDetails() {
			private static final long serialVersionUID = -1260132390884896961L;

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				String[] roles = Authority.toStringArray(u.getAuthorities());
				return AuthorityUtils.createAuthorityList(roles);
			}

			@Override
			public String getPassword() {
				return u.getPassword();
			}

			@Override
			public String getUsername() {
				return u.getEmail();
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return u.getEnabled();
			}
			
		};
	}
}
