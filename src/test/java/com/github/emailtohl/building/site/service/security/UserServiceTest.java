package com.github.emailtohl.building.site.service.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.emailtohl.building.site.entities.User;
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
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityTestConfig.class);
		userService = context.getBean(UserService.class);
		authenticationManager = context.getBean(AuthenticationManager.class);
	}

	private void setEmailtohl() {
		SecurityContextHolder.clearContext();
		String name = "emailtohl@163.com";
		String password = "123456";
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	private void setFoo() {
		SecurityContextHolder.clearContext();
		String name = "foo@test.com";
		String password = "123456";
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	private void setBar() {
		SecurityContextHolder.clearContext();
		String name = "bar@test.com";
		String password = "123456";
		Authentication token = new UsernamePasswordAuthenticationToken(name, password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	@Test
	public void testAddUser() {
		userService.addUser(new User());
	}

	@Test
	public void testEnableUser() {
		userService.enableUser(1000L);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDisableUser1() {
		SecurityContextHolder.clearContext();
		userService.deleteUser(1000L);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testDisableUser2() {
		setBar();
		userService.deleteUser(1000L);
	}

	@Test
	public void testDisableUser3() {
		setEmailtohl();
		userService.deleteUser(1000L);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testMergeUser1() {
		SecurityContextHolder.clearContext();
		User u = new User();
		userService.mergeUser(1000L, u);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testMergeUser2() {
		setBar();
		User u = new User();
		userService.mergeUser(1000L, u);
	}
	
	@Test
	public void testMergeUser3() {
		setEmailtohl();
		User u = new User();
		u.setEmail("bar@test.com");
		userService.mergeUser(1000L, u);
	}
	
	@Test
	public void testChangePassword1() {
		setEmailtohl();
		userService.changePassword("emailtohl@163.com", "987654321");
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testChangePassword2() {
		setEmailtohl();
		userService.changePassword("foo@test.com", "987654321");
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDeleteUser1() {
		SecurityContextHolder.clearContext();
		userService.deleteUser(1000L);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testDeleteUser2() {
		setFoo();
		userService.deleteUser(1000L);
	}
	
	@Test
	public void testDeleteUser3() {
		setEmailtohl();
		userService.deleteUser(1000L);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUser1() {
		SecurityContextHolder.clearContext();
		userService.getUser(1000L);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testGetUser2() {
		setBar();
		userService.getUser(1000L);
	}
	
	@Test
	public void testGetUser3() {
		setBar();
		userService.getUser(2000L);
	}
	
	@Test
	public void testGetUser4() {
		setFoo();
		userService.getUser(1000L);
		setEmailtohl();
		userService.getUser(1000L);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserByEmail1() {
		SecurityContextHolder.clearContext();
		userService.getUserByEmail("emailtohl@163.com");
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testGetUserByEmail2() {
		setBar();
		userService.getUserByEmail("emailtohl@163.com");
	}
	
	@Test
	public void testGetUserByEmail3() {
		setEmailtohl();
		userService.getUserByEmail("emailtohl@163.com");
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserPager1() {
		SecurityContextHolder.clearContext();
		userService.getUserPager(null, null);
	}
	
	@Test
	public void testGetUserPager2() {
		setBar();
		userService.getUserPager(null, null);
	}

}
