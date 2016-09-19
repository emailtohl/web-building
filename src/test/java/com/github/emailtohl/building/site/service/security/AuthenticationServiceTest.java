package com.github.emailtohl.building.site.service.security;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_DEVELPMENT;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;
import static org.junit.Assert.assertTrue;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SecurityTestConfig.class)
@ActiveProfiles({ PROFILE_DEVELPMENT, PROFILE_QA })
public class AuthenticationServiceTest {
	@Inject
	UserService userService;
	@Inject
	AuthenticationService authenticationService;
	@Inject
	AuthenticationManager authenticationManager;

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetPageByAuthorities1() {
		SecurityContextHolder.clearContext();
		User u = new User();
		authenticationService.getPageByAuthorities(u, new PageRequest(0, 20));
	}

	@Test(expected = AccessDeniedException.class)
	public void testGetPageByAuthorities2() {
		setBar();
		User u = new User();
		authenticationService.getPageByAuthorities(u, new PageRequest(0, 20));
	}

	@Test
	public void testGetPageByAuthorities3() {
		setEmailtohl();
		User u = new User();
		u.setEmail("bar@test.com");
		authenticationService.getPageByAuthorities(u, new PageRequest(0, 20));
	}

	@Test
	public void testAuthenticate() {
		Authentication auth = authenticationService.authenticate("foo@test.com", "123456");
		assertTrue(auth.isAuthenticated());
	}
/*	注册用户时需要开通基本授权，所以策略发生变化，完全由代码控制
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGrantedAuthority1() {
		SecurityContextHolder.clearContext();
		authenticationService.grantedAuthority(1000L, new HashSet<Authority>(Arrays.asList(Authority.ADMIN, Authority.MANAGER)));
	}
*/
	@Test(expected = AccessDeniedException.class)
	public void testGrantedAuthority2() {
		User u = new User();
		setBar();
		userService.mergeUser(1000L, u);
	}

	@Test
	public void testGrantedAuthority3() {
		User u = new User();
		setFoo();
		userService.mergeUser(1000L, u);
	}

	@Test
	public void testGrantedAuthority4() {
		User u = new User();
		setEmailtohl();
		userService.mergeUser(1000L, u);
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
}
