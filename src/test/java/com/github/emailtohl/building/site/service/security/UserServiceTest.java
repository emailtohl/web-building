package com.github.emailtohl.building.site.service.security;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.emailtohl.building.site.service.UserService;
/**
 * 测试spring security的配置
 * @author Helei
 */
public class UserServiceTest {
	UserService userService;
	AuthenticationManager authenticationManager;
	
	@SuppressWarnings("resource")
	@Before
	public void setUp() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		userService = context.getBean(UserService.class);
		authenticationManager = context.getBean(AuthenticationManager.class);
	}

	@Test
	public void testAddUser() {
		userService.addUser(null);
	}

//	@Test
	public void testEnableUser() {
		fail("Not yet implemented");
	}

//	@Test
	public void testDisableUser() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGrantedAuthority() {
		fail("Not yet implemented");
	}

//	@Test
	public void testMergeUser() {
		fail("Not yet implemented");
	}

//	@Test
	public void testChangePassword() {
		List<GrantedAuthority> ls = AuthorityUtils.createAuthorityList("ADMIN", "EMPLOYEE", "MANAGER", "USER");
//		authenticationManager.authenticate(authentication);
//		SecurityContext context = new SecurityContext();
//		SecurityContextHolder.setContext(context);
		System.out.println(SecurityContextHolder.getContext().getAuthentication());
//		AuthenticationManager manager = 
//		Authentication auth = new Authentication();
		userService.changePassword(null, null);
	}

//	@Test
	public void testDeleteUser() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGetUser() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGetUserPager() {
		fail("Not yet implemented");
	}

//	@Test
	public void testAuthenticate() {
		userService.authenticate(null, null);
	}

}
