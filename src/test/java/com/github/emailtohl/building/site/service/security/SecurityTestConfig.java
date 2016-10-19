package com.github.emailtohl.building.site.service.security;

import static com.github.emailtohl.building.initdb.PersistenceData.*;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;

import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserPermissionEvaluator;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
/**
 * 为测试spring security注解在方法级别的配置
 * @author HeLei
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
	public ServiceStub serviceStub() {
		return new ServiceStub();
	}
	
	@Bean
	public UserService userService() {
		return serviceStub().getUserService();
	}
	
	@Bean
	public SecurityContextManager securityContextManager() throws Exception {
		return new SecurityContextManager(authenticationManager());
	}
	
	@Bean
	public AuthenticationService authenticationService() throws Exception {
		AuthenticationManager authenticationManager = authenticationManager();
		return new AuthenticationService() {
			UserDto emailtohlDto = new UserDto(), fooDto = new UserDto(), barDto = new UserDto();
			{
				BeanUtils.copyProperties(emailtohl, emailtohlDto);
				BeanUtils.copyProperties(foo, fooDto);
				BeanUtils.copyProperties(bar, barDto);
			}
			
			private final Logger logger = LogManager.getLogger();
			@Override
			public Authentication authenticate(String email, String password) {
				logger.debug("authenticate invoked");
				Authentication token = new UsernamePasswordAuthenticationToken(email, password);
				return authenticationManager.authenticate(token);
			}


			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				UsernamePasswordAuthenticationToken credentials = (UsernamePasswordAuthenticationToken) authentication;
				String email = credentials.getPrincipal().toString();
				String password = credentials.getCredentials().toString();
				return this.authenticate(email, password);
			}

			@Override
			public boolean supports(Class<?> authentication) {
				return authentication == UsernamePasswordAuthenticationToken.class;
			}

			@Override
			public boolean hasAuthority(Authority... authorities) {
				logger.debug("hasAuthority invoked");
				return false;
			}
			
			@Override
			public Pager<UserDto> getPageByRoles(UserDto user, Pageable pageable) {
				logger.debug("getUserPager invoked");
				Pager<UserDto> p = new Pager<UserDto>(Arrays.asList(emailtohlDto, fooDto, barDto), 100L);
				return p;
			}

			@Override
			public boolean isExist(String email) {
				logger.debug("isExist invoked");
				if ("emailtohl@163.com".equals(email) || "foo@test.com".equals(email) || "bar@test.com".equals(email))
					return true;
				else
					return false;
			}
			
			@Override
			public void changePassword(String email, String newPassword) {
				logger.debug("changePassword invoked");
			}

			@Override
			public void grantedRoles(Long id, Set<String> roleNames) {
				logger.debug("grantedAuthority invoked");
			}

			/*@Override
			public long updateUserType(long id, String role) {
				logger.debug("updateUserType invoked");
				return 0;
			}*/
			
		};
	}
}
