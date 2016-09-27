package com.github.emailtohl.building.site.service.security;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;
/**
 * 测试spring security的配置
 * @author Helei
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SecurityTestConfig.class)
public class UserServiceTest {
	@Inject
	UserService userService;
	@Inject
	AuthenticationManager authenticationManager;

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
		Manager u = new Manager();
		userService.mergeUser(1000L, u);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testMergeUser2() {
		setBar();
		Manager u = new Manager();
		userService.mergeUser(1000L, u);
	}
	
	@Test
	public void testMergeUser3() {
		setEmailtohl();
		Manager u = new Manager();
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
		userService.getUserPager(new User(), new PageRequest(10, 20));
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserPage1() {
		SecurityContextHolder.clearContext();
		userService.getUserPage(new User(), new PageRequest(10, 20));
	}
	
	@Test
	public void testGetUserPage2() {
		setBar();
		userService.getUserPage(new User(), new PageRequest(10, 20));
	}

}
