package com.github.emailtohl.building.site.service.security;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.entities.user.User;
import com.github.emailtohl.building.site.service.user.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
/**
 * 测试spring security的配置
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
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
		userService.addEmployee(serviceStub.employee);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAddEmployee2() {
		securityContextManager.setBaz();
		userService.addEmployee(serviceStub.employee);
	}
	
	@Test
	public void testAddEmployee3() {
		securityContextManager.setFoo();
		userService.addEmployee(serviceStub.employee);
		securityContextManager.setEmailtohl();
		userService.addEmployee(serviceStub.employee);
	}
	
	@Test
	public void testaddCustomer() {
		userService.addCustomer(serviceStub.customer);
	}
	
	@Test
	public void testEnableUser() {
		userService.enableUser(serviceStub.customerId);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDisableUser1() {
		securityContextManager.clearContext();
		userService.deleteUser(serviceStub.customerId);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testDisableUser2() {
		securityContextManager.setBar();
		userService.deleteUser(serviceStub.customerId);
	}

	@Test
	public void testDisableUser3() {
		securityContextManager.setEmailtohl();
		userService.deleteUser(serviceStub.customerId);
	}
	
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGrantRoles1() {
		securityContextManager.clearContext();
		userService.grantRoles(serviceStub.customerId);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testGrantRoles2() {
		securityContextManager.setBaz();
		userService.grantRoles(serviceStub.customerId);
	}

	@Test
	public void testGrantRoles3() {
		securityContextManager.setEmailtohl();
		userService.grantRoles(serviceStub.customerId);
	}
	
	@Test
	public void testGrantUserRole() {
		securityContextManager.clearContext();
		userService.grantUserRole(serviceStub.customerId);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testChangePassword1() {
		securityContextManager.clearContext();
		userService.changePassword(serviceStub.customer.getEmail(), serviceStub.testPassword);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testChangePassword2() {
		securityContextManager.setEmailtohl();
		userService.changePassword(serviceStub.customer.getEmail(), serviceStub.testPassword);
	}
	
	@Test
	public void testChangePassword3() {
		securityContextManager.setBaz();
		userService.changePassword(serviceStub.customer.getEmail(), serviceStub.testPassword);
	}
	
	@Test
	public void testChangePasswordByEmail() {
		securityContextManager.clearContext();
		userService.changePasswordByEmail(serviceStub.customer.getEmail(), serviceStub.testPassword);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDeleteUser1() {
		SecurityContextHolder.clearContext();
		userService.deleteUser(serviceStub.customerId);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testDeleteUser2() {
		securityContextManager.setFoo();
		userService.deleteUser(serviceStub.customerId);
	}
	
	@Test
	public void testDeleteUser3() {
		securityContextManager.setEmailtohl();
		userService.deleteUser(serviceStub.customerId);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUser1() {
		SecurityContextHolder.clearContext();
		userService.getUser(serviceStub.customerId);
	}
	
	@Test
	public void testGetUser2() {
		securityContextManager.setBaz();
		userService.getUser(serviceStub.customerId);
	}
	
	@Test
	public void testGetUser3() {
		securityContextManager.setBar();
		userService.getUser(serviceStub.customerId);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserByEmail1() {
		SecurityContextHolder.clearContext();
		userService.getUserByEmail(serviceStub.customer.getEmail());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testGetUserByEmail2() {
		securityContextManager.setBar();
		User u = userService.getUserByEmail(serviceStub.employee.getEmail());
		System.out.println(u);
		securityContextManager.setBaz();
		u = userService.getUserByEmail(serviceStub.customer.getEmail());
		System.out.println(u);
		u = userService.getUserByEmail(serviceStub.employee.getEmail());
		System.out.println(u);
	}

	@Test
	public void testGetUserByEmail3() {
		securityContextManager.setEmailtohl();
		userService.getUserByEmail(serviceStub.customer.getEmail());
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testMergeEmployee1() {
		SecurityContextHolder.clearContext();
		userService.mergeEmployee(serviceStub.employee.getEmail(), serviceStub.employee);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testMergeEmployee2() {
		securityContextManager.setBar();
		userService.mergeEmployee(serviceStub.employee.getEmail(), serviceStub.employee);
	}
	
	@Test
	public void testMergeEmployee3() {
		securityContextManager.setFoo();
		userService.mergeEmployee(serviceStub.employee.getEmail(), serviceStub.employee);
		
		securityContextManager.setEmailtohl();
		userService.mergeEmployee(serviceStub.employee.getEmail(), serviceStub.employee);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testMergeCustomer1() {
		SecurityContextHolder.clearContext();
		userService.mergeCustomer(serviceStub.customer.getEmail(), serviceStub.customer);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testMergeCustomer2() {
		securityContextManager.setBar();
		userService.mergeCustomer(serviceStub.customer.getEmail(), serviceStub.customer);
	}
	
	@Test
	public void testMergeCustomer3() {
		securityContextManager.setBaz();
		userService.mergeCustomer(serviceStub.customer.getEmail(), serviceStub.customer);
		
		securityContextManager.setEmailtohl();
		userService.mergeCustomer(serviceStub.customer.getEmail(), serviceStub.customer);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testUpdateIconSrc1() {
		securityContextManager.clearContext();
		userService.updateIconSrc(serviceStub.employeeId, "url");
	}
	
	@Test
	public void testUpdateIconSrc2() {
		securityContextManager.setFoo();
		userService.updateIconSrc(serviceStub.employeeId, "url");
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testUpdateIcon1() {
		securityContextManager.clearContext();
		userService.updateIcon(serviceStub.employeeId, new byte[1]);
	}
	
	@Test
	public void testUpdateIcon2() {
		securityContextManager.setFoo();
		userService.updateIcon(serviceStub.employeeId, new byte[1]);
	}
	
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserPager1() {
		SecurityContextHolder.clearContext();
		userService.getUserPager(null, null);
	}
	
	@Test
	public void testGetUserPager2() {
		securityContextManager.setBar();
		userService.getUserPager(serviceStub.customer, serviceStub.pageable);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetUserPage1() {
		SecurityContextHolder.clearContext();
		userService.getUserPage(serviceStub.customer, serviceStub.pageable);
	}
	
	@Test
	public void testGetUserPage2() {
		securityContextManager.setBar();
		userService.getUserPage(serviceStub.customer, serviceStub.pageable);
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testGetRoles1() {
		securityContextManager.clearContext();
		userService.getRoles();
	}
	
	@Test
	public void testGetRoles2() {
		securityContextManager.setBaz();
		userService.getRoles();
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testSetPublicKey1() {
		securityContextManager.clearContext();
		userService.setPublicKey("eyJtb2R1bGUiOiI5MTIxMzkwMjU2NDM1ODA1MjM4NDg4MDI5NDE3MjgxMzIxNjk4NTYxMDk2NTcwNTE5NDc2OTM4NDQ4NDA1NzgxMjAyMDM4NzM1NzQwNDg0OTczODQ5NzU2MTIzNjE3MjQ1MzI1MzMzMTEzNDMwMzAwMjc4NjIyNjc2NjkwMDEzMzkxOTgxMjAyMTk2NzY5Mjg2MDc3NzMwODkwOTkxODIyMDMzNTk4NjQ1NjkwMzU1NzYxNTU3NjUwNjkwMzI1MTE2NTUzODQ3OTI0NTc5OTk1MTQwNDM0NDkyOTk3NDg0MDg1NjM5ODI2NjU4NzY1NDM4NTE3ODk0Mzg5NTc4NDg1ODYxNDMxMjY3Mzg0OTM3MDE1MzgyMjg2MzAzODYxOTU5NzcyOTA1OTQwNDUzNjMxNjA2OSIsInB1YmxpY0tleSI6IjY1NTM3In0=");
	}
	
	@Test
	public void testSetPublicKey2() {
		securityContextManager.setBaz();
		userService.setPublicKey("eyJtb2R1bGUiOiI5MTIxMzkwMjU2NDM1ODA1MjM4NDg4MDI5NDE3MjgxMzIxNjk4NTYxMDk2NTcwNTE5NDc2OTM4NDQ4NDA1NzgxMjAyMDM4NzM1NzQwNDg0OTczODQ5NzU2MTIzNjE3MjQ1MzI1MzMzMTEzNDMwMzAwMjc4NjIyNjc2NjkwMDEzMzkxOTgxMjAyMTk2NzY5Mjg2MDc3NzMwODkwOTkxODIyMDMzNTk4NjQ1NjkwMzU1NzYxNTU3NjUwNjkwMzI1MTE2NTUzODQ3OTI0NTc5OTk1MTQwNDM0NDkyOTk3NDg0MDg1NjM5ODI2NjU4NzY1NDM4NTE3ODk0Mzg5NTc4NDg1ODYxNDMxMjY3Mzg0OTM3MDE1MzgyMjg2MzAzODYxOTU5NzcyOTA1OTQwNDUzNjMxNjA2OSIsInB1YmxpY0tleSI6IjY1NTM3In0=");
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testDeletePublicKey1() {
		securityContextManager.clearContext();
		userService.clearPublicKey();
	}
	
	@Test
	public void testDeletePublicKey2() {
		securityContextManager.setBaz();
		userService.clearPublicKey();
	}

}
