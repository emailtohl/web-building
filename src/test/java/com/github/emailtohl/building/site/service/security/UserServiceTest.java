package com.github.emailtohl.building.site.service.security;
import static com.github.emailtohl.building.initdb.PersistenceData.baz;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
/**
 * 测试spring security的配置
 * @author HeLei
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class UserServiceTest {
	/**
	 * serviceStub.testUserDto引用的是baz
	 */
	@Inject ServiceStub serviceStub;
	@Inject SecurityContextManager securityContextManager;
	@Inject
	@Named("userServiceMock")
	UserService userService;

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testAddEmployee1() {
		securityContextManager.clearContext();
		userService.addEmployee(foo);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAddEmployee2() {
		securityContextManager.setBar();
		userService.addEmployee(foo);
	}
	
	@Test
	public void testAddEmployee3() {
		securityContextManager.setFoo();
		userService.addEmployee(foo);
		securityContextManager.setEmailtohl();
		userService.addEmployee(foo);
	}
	
	@Test
	public void testaddCustomer() {
		userService.addCustomer(baz);
	}
	
	@Test
	public void testEnableUser() {
		userService.enableUser(serviceStub.testId);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDisableUser1() {
		securityContextManager.clearContext();
		userService.deleteUser(serviceStub.testId);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testDisableUser2() {
		securityContextManager.setBar();
		userService.deleteUser(serviceStub.testId);
	}

	@Test
	public void testDisableUser3() {
		securityContextManager.setEmailtohl();
		userService.deleteUser(serviceStub.testId);
	}
	
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGrantRoles1() {
		securityContextManager.clearContext();
		userService.grantRoles(serviceStub.testId);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testGrantRoles2() {
		securityContextManager.setBaz();
		userService.grantRoles(serviceStub.testId);
	}

	@Test
	public void testGrantRoles3() {
		securityContextManager.setEmailtohl();
		userService.grantRoles(serviceStub.testId);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testChangePassword1() {
		securityContextManager.clearContext();
		userService.changePassword(serviceStub.testUserDto.getEmail(), serviceStub.testPassword);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testChangePassword2() {
		securityContextManager.setEmailtohl();
		userService.changePassword(serviceStub.testUserDto.getEmail(), serviceStub.testPassword);
	}
	
	@Test
	public void testChangePassword3() {
		securityContextManager.setBaz();
		userService.changePassword(serviceStub.testUserDto.getEmail(), serviceStub.testPassword);
	}
	
	@Test
	public void testChangePasswordByEmail() {
		securityContextManager.clearContext();
		userService.changePasswordByEmail(serviceStub.testUserDto.getEmail(), serviceStub.testPassword);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDeleteUser1() {
		SecurityContextHolder.clearContext();
		userService.deleteUser(serviceStub.testId);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testDeleteUser2() {
		securityContextManager.setFoo();
		userService.deleteUser(serviceStub.testId);
	}
	
	@Test
	public void testDeleteUser3() {
		securityContextManager.setEmailtohl();
		userService.deleteUser(serviceStub.testId);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUser1() {
		SecurityContextHolder.clearContext();
		userService.getUser(serviceStub.testId);
	}
	
	@Test
	public void testGetUser2() {
		securityContextManager.setBaz();
		userService.getUser(serviceStub.testId);
	}
	
	@Test
	public void testGetUser3() {
		securityContextManager.setBar();
		userService.getUser(serviceStub.testId);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserByEmail1() {
		SecurityContextHolder.clearContext();
		userService.getUserByEmail(serviceStub.testUserDto.getEmail());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testGetUserByEmail2() {
		securityContextManager.setBar();
		userService.getUserByEmail(serviceStub.testUserDto.getEmail());
	}
	
	@Test
	public void testGetUserByEmail3() {
		securityContextManager.setBaz();
		userService.getUserByEmail(baz.getEmail());
	}
	
	@Test
	public void testGetUserByEmail4() {
		securityContextManager.setEmailtohl();
		userService.getUserByEmail(baz.getEmail());
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testMergeUser1() {
		SecurityContextHolder.clearContext();
		userService.mergeUser(serviceStub.testUserDto.getEmail(), serviceStub.testUserDto);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testMergeUser2() {
		securityContextManager.setBar();
		userService.mergeUser(serviceStub.testUserDto.getEmail(), serviceStub.testUserDto);
	}
	
	@Test
	public void testMergeUser3() {
		securityContextManager.setBaz();
		userService.mergeUser(serviceStub.testUserDto.getEmail(), serviceStub.testUserDto);
		
		securityContextManager.setEmailtohl();
		userService.mergeUser(serviceStub.testUserDto.getEmail(), serviceStub.testUserDto);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserPager1() {
		SecurityContextHolder.clearContext();
		userService.getUserPager(null, null);
	}
	
	@Test
	public void testGetUserPager2() {
		securityContextManager.setBar();
		userService.getUserPager(serviceStub.testUserDto, new PageRequest(10, 20));
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserPage1() {
		SecurityContextHolder.clearContext();
		userService.getUserPage(serviceStub.testUserDto, new PageRequest(10, 20));
	}
	
	@Test
	public void testGetUserPage2() {
		securityContextManager.setBar();
		userService.getUserPage(serviceStub.testUserDto, new PageRequest(10, 20));
	}

}
