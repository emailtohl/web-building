package com.github.emailtohl.building.site.service.security;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;

import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserPermissionEvaluator;
import com.github.emailtohl.building.site.service.UserService;
/**
 * 为测试spring security注解在方法级别的配置
 * @author Helei
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityTestConfig extends GlobalMethodSecurityConfiguration {
	
	/**
	 * 使用示例
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityTestConfig.class);
		AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);
		String name = "emailtohl@163.com";
		String password = "123456";
		// (1)将用户名、密码封装成一个token
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		try {
			// (2)将token传给AuthenticationManager进行身份认证
			// (3)认证完毕，返回一个认证后的身份：
			Authentication result = authenticationManager.authenticate(token);
			// (4)认证后，存储到SecurityContext里
			SecurityContextHolder.getContext().setAuthentication(result);
			System.out.println("认证成功，认证消息添加到安全上下文中");
			System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		} catch (BadCredentialsException e) {
			System.err.println("认证失败");
		}
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.inMemoryAuthentication().withUser("emailtohl@163.com").password("123456").authorities("USER", "ADMIN")
				.and().withUser("foo@test.com").password("123456").authorities("MANAGER")
				.and().withUser("bar@test.com").password("123456").authorities("EMPLOYEE");
	}
	
	/**
	 * 将认证管理器注册进Spring
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	/**
	 * 自定义访问许可，需要实现PermissionEvaluator接口
	 * 然后在@PreAuthorize注解中可以调用PermissionEvaluator接口中的方法：
	 * hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
	 * hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
	 */
	@Override
	public MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator(new UserPermissionEvaluator());
		return handler;
	}
	
	@Bean
	public UserService userService() throws Exception {
		return new UserService() {
			private final Logger logger = LogManager.getLogger();
			
			@Override
			public Long addUser(User u) {
				logger.debug("addUser invoked");
				return 1000L;
			}

			@Override
			public void enableUser(Long id) {
				logger.debug("enableUser invoked");
			}

			@Override
			public void disableUser(Long id) {
				logger.debug("disableUser invoked");
			}

			@Override
			public void mergeUser(Long id, User u) {
				logger.debug("mergeUser invoked");
			}

			@Override
			public void changePassword(String email, String newPassword) {
				logger.debug("changePassword invoked");
			}

			@Override
			public void deleteUser(Long id) {
				logger.debug("deleteUser invoked");
			}

			@Override
			public User getUser(Long id) {
				logger.debug("getUser invoked");
				if (id == 1000L) {
					return emailtohl;
				} else {
					return bar;
				}
			}

			@Override
			public Page<User> getUserPager(User u, Pageable pageable) {
				logger.debug("getUserPager invoked");
				return new PageImpl<User>(Arrays.asList(emailtohl, foo, bar));
			}

			@Override
			public User getUserByEmail(String email) {
				return emailtohl;
			}
			
		};
	}

	@Bean
	public AuthenticationService authenticationService() throws Exception {
		AuthenticationManager authenticationManager = authenticationManager();
		return new AuthenticationService() {
			private final Logger logger = LogManager.getLogger();
			@Override
			public Authentication authenticate(String email, String password) {
				logger.debug("authenticate invoked");
				Authentication token = new UsernamePasswordAuthenticationToken(email, password);
				return authenticationManager.authenticate(token);
			}

			@Override
			public void grantedAuthority(Long id, Set<Authority> authorities) {
				logger.debug("grantedAuthority invoked");
			}
		};
	}
}
