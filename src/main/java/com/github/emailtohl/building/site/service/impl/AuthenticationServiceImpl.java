package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.site.entities.Role.ADMIN;
import static com.github.emailtohl.building.site.entities.Role.EMPLOYEE;
import static com.github.emailtohl.building.site.entities.Role.MANAGER;
import static com.github.emailtohl.building.site.entities.Role.USER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
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

import com.github.emailtohl.building.common.Constant;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.BCryptUtil;
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.mail.EmailService;
import com.github.emailtohl.building.site.service.AuthenticationService;

/**
 * 认证服务的实现类
 * 主要是为了实践自定义的AuthenticationProvider，用于spring security的配置中
 * @author HeLei
 */
@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {
	private static final Logger logger = LogManager.getLogger();
	@Inject UserRepository userRepository;
	@Inject EmailService emailService;
	@Inject RoleRepository roleRepository;
	
	Role admin, employee, manager, user;
	Map<String, Role> roleMap = new HashMap<String, Role>();
	
	@PostConstruct
	public void setRoles() {
		admin = roleRepository.findByName(ADMIN);
		employee = roleRepository.findByName(EMPLOYEE);
		manager = roleRepository.findByName(MANAGER);
		user = roleRepository.findByName(USER);
		roleMap.put(ADMIN, admin);
		roleMap.put(EMPLOYEE, employee);
		roleMap.put(MANAGER, manager);
		roleMap.put(USER, user);
	}
	
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
				Set<String> authorities = new HashSet<String>();
				for (Authority a : u.authoritySet()) {
					authorities.add(a.getName());
				}
				return AuthorityUtils.createAuthorityList(authorities.toArray(new String[authorities.size()]));
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
				return new UserDetailsImpl(u);
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

	/**
	 * 判断用户是否具有该权限
	 * @param authorities
	 * @return
	 */
	@Override
	public boolean hasAuthority(Authority... authorities) {
		boolean result = false;
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a != null) {
			Set<String> grantedAuthoritySet = getGrantedAuthoritySet(a.getAuthorities());
			for (int i = 0; i < authorities.length; i++) {
				String authority = authorities[i].getName();
				if (grantedAuthoritySet.contains(authority)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	private Set<String> getGrantedAuthoritySet(Collection<? extends GrantedAuthority> collection) {
		Set<String> set = new HashSet<String>();
		for (GrantedAuthority g : collection) {
			set.add(g.getAuthority());
		}
		return set;
	}
	
	/**
	 * 查询用户权限，根据用户权限过滤信息
	 */
	@Override
	public Pager<UserDto> getPageByRoles(UserDto user, Pageable pageable) {
		String email = user.getEmail();
		if (email != null && !email.isEmpty()) {
			email = '%' + email + '%';
		}
		user.setEmail(email);
		Pager<User> p = userRepository.getPagerByCriteria(user, pageable);
		List<User> src = p.getContent();
		List<UserDto> dest = new ArrayList<UserDto>();
		src.forEach(u -> {
			UserDto target = new UserDto();
			BeanUtils.copyProperties(u, target, "password", "icon", "subsidiary", "iconSrc");
		});
		return new Pager<UserDto>(dest, p.getTotalElements(), pageable.getPageNumber(), p.getPageSize());
	}
	
	/**
	 * 给用户授权
	 */
	@Override
	public void grantedRoles(Long id, Set<String> roleNames) {
		// 用户注册时，不含任何权限，但是需要授予普通用户（User）权限，所以只要含有ADMIN, MANAGER, EMPLOYEE中任意一个权限，则抛异常
		/*if (!hasAuthority(ADMIN, MANAGER, EMPLOYEE, USER) && !(authorities.contains(ADMIN) || authorities.contains(MANAGER) || authorities.contains(EMPLOYEE))) {
			throw new AccessDeniedException("没有授予" + authorities + "的权限");
		}
		// 如果没有ADMIN的权限，则不能授予ADMIN的权限
		if (!hasAuthority(ADMIN) && authorities.contains(ADMIN)) {
			throw new AccessDeniedException("不能添加ADMIN权限");
		}
		// 如果既不包含ADMIN也不包含MANAGER的权限，那么就不能授予MANAGER权限
		if (!hasAuthority(ADMIN, MANAGER) && (authorities.contains(MANAGER))) {
			throw new AccessDeniedException("没有权限添加MANAGER权限");
		}*/
		Set<Role> roles = new HashSet<Role>();
		roleNames.forEach(s -> {
			roles.add(roleMap.get(s));
		});
		userRepository.findOne(id).setRoles(roles);
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
	private Pattern p = Pattern.compile("username=(" + Constant.PATTERN_EMAIL.substring(1, Constant.PATTERN_EMAIL.length() - 1) + ')');
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Matcher m = p.matcher(username);
		if (!m.find()) {
			return null;
		}
		String email = m.group(1);
		final User u = userRepository.findByEmail(email);
		return new UserDetailsImpl(u);
	}
	
	/**
	 * 自定义UserDetails的实现，这是Spring Security认证管理器要访问的数据结构
	 * 本实现提供的getter方法符合JavaBean规范，所以认证管理器就能获知其属性
	 * 例如getUsername()代表该JavaBean的属性是“username”，值是执行后的结果
	 * @author HeLei
	 *
	 */
	class UserDetailsImpl implements UserDetails {
		private static final long serialVersionUID = -1260132390884896961L;
		private User u;
		private Long id;
		private String username;
		private boolean enabled;
		private String email;
		private List<GrantedAuthority> authorities;
		private String iconSrc;
		
		UserDetailsImpl(User u) {
			this.u = u;
			this.id = u.getId();
			this.email = u.getEmail();
			this.username = this.email;
			this.enabled = u.getEnabled();
			this.iconSrc = u.getIconSrc();
			Set<String> stringAuthorities = u.authorities();
			this.authorities = AuthorityUtils.createAuthorityList(stringAuthorities.toArray(new String[stringAuthorities.size()]));
			
		}
		/**
		 * 新增一个id属性
		 * @return
		 */
		public Long getId() {
			return this.id;
		}
		
		/**
		 * 新增一个图片地址属性
		 */
		public String getIconSrc() {
			return this.iconSrc;
		}
		
		/**
		 * 新增一个email属性
		 */
		public String getEmail() {
			return this.email;
		}
		
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return this.authorities;
		}

		@Override
		public String getPassword() {
			return u.getPassword();
		}

		@Override
		public String getUsername() {
			return this.username;
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
			return this.enabled;
		}
	}
	
	@Override
	public boolean isExist(String email) {
		if (userRepository.findByEmail(email) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void changePassword(String email, String newPassword) {
		String hashPw = BCryptUtil.hash(newPassword);
		User u = userRepository.findByEmail(email);
		u.setPassword(hashPw);
	}

	@Override
	public long updateUserType(long id, String role) {
		User persist = userRepository.getOne(id);
		User entity;
		switch (role) {
		case EMPLOYEE :
			entity = new Employee();
			break;
		case MANAGER :
			entity = new Manager();
			break;
		default :
			entity = new User();
			break;
		}
		BeanUtils.copyProperties(persist, entity, "id");
		userRepository.delete(persist);
		userRepository.flush();
		userRepository.save(entity);
		return entity.getId();
	}

}
