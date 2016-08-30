package com.github.emailtohl.building.site.service.security;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;

import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Config extends GlobalMethodSecurityConfiguration {
	
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
	
	@Bean
	public UserService userService() {
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
			public void grantedAuthority(Long id, Set<Authority> authorities) {
				logger.debug("grantedAuthority invoked");
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
				return emailtohl;
			}

			@Override
			public Page<User> getUserPager(User u, Pageable pageable) {
				logger.debug("getUserPager invoked");
				return new PageImpl<User>(Arrays.asList(emailtohl, foo, bar));
			}

			@Override
			public User authenticate(String email, String password) {
				logger.debug("authenticate invoked");
				return emailtohl;
			}
			
		};
	}
}
