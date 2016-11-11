package com.github.emailtohl.building.site.service.security;
import static com.github.emailtohl.building.site.entities.Authority.USER_DELETE;
import static com.github.emailtohl.building.site.entities.Authority.USER_UPDATE_ALL;

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
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
/**
 * 测试spring security的配置
 * @author HeLei
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
	
	@Test
	public void testGetUserByEmail2() {
		securityContextManager.setBar();
		userService.getUserByEmail(serviceStub.customer.getEmail());
		securityContextManager.setBaz();
		userService.getUserByEmail(serviceStub.customer.getEmail());
	}
	
	@Test
	public void testGetUserByEmail4() {
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

	@Test
	public void testHasAuthority() {
		securityContextManager.clearContext();
		userService.hasAuthority(USER_DELETE, USER_UPDATE_ALL);
	}
}
