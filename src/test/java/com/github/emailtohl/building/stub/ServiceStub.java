package com.github.emailtohl.building.stub;

import static com.github.emailtohl.building.initdb.PersistenceData.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.mail.EmailService;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 为项目测试提供统一的桩支持
 * @author HeLei
 */
@SuppressWarnings("unused")
public class ServiceStub {
	private static final Logger logger = LogManager.getLogger();
	public final Employee employee = foo;
	public final long employeeId = 200L;
	public final Customer customer = baz;
	public final long customerId = 100L;
	public final String testPassword = "123456";
	public final Pageable pageable = new PageRequest(0, 20);
	
	public UserService getUserService() {
		UserService userService = mock(UserService.class);
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		when(userService.addEmployee(employee)).thenReturn(employeeId);
		when(userService.addCustomer(customer)).thenReturn(customerId);
		doAnswer(answer).when(userService).enableUser(employeeId);
		doAnswer(answer).when(userService).enableUser(customerId);
		doAnswer(answer).when(userService).disableUser(employeeId);
		doAnswer(answer).when(userService).disableUser(customerId);
		doAnswer(answer).when(userService).grantRoles(employeeId);
		doAnswer(answer).when(userService).grantRoles(customerId);
		doAnswer(answer).when(userService).changePassword(employee.getEmail(), testPassword);
		doAnswer(answer).when(userService).changePassword(customer.getEmail(), testPassword);
		doAnswer(answer).when(userService).changePasswordByEmail(employee.getEmail(), testPassword);
		doAnswer(answer).when(userService).changePasswordByEmail(customer.getEmail(), testPassword);
		doAnswer(answer).when(userService).deleteUser(employeeId);
		doAnswer(answer).when(userService).deleteUser(customerId);
		when(userService.getUser(employeeId)).thenReturn(employee);
		when(userService.getUser(customerId)).thenReturn(customer);
		when(userService.getUserByEmail(employee.getEmail())).thenReturn(customer);
		when(userService.getUserByEmail(customer.getEmail())).thenReturn(customer);
		doAnswer(answer).when(userService).mergeEmployee(employee.getEmail(), employee);
		doAnswer(answer).when(userService).mergeCustomer(customer.getEmail(), customer);
		Pager<User> employeePager = new Pager<User>(Arrays.asList(employee));
		Pager<User> customerPager = new Pager<User>(Arrays.asList(customer));
		when(userService.getUserPager(employee, pageable)).thenReturn(employeePager);
		when(userService.getUserPager(customer, pageable)).thenReturn(customerPager);
		Page<User> employeePage = new PageImpl<User>(Arrays.asList(employee));
		Page<User> customerPage = new PageImpl<User>(Arrays.asList(customer));
		when(userService.getUserPage(employee, pageable)).thenReturn(employeePage);
		when(userService.getUserPage(customer, pageable)).thenReturn(customerPage);
		when(userService.isExist(employee.getEmail())).thenReturn(true);
		when(userService.isExist(customer.getEmail())).thenReturn(true);
		when(userService.getPageByRoles(employee.getEmail(), employee.getRoles(), pageable)).thenReturn(employeePager);
		when(userService.getPageByRoles(customer.getEmail(), customer.getRoles(), pageable)).thenReturn(customerPager);
		when(userService.authenticate(employee.getEmail(), testPassword)).thenReturn(employee);
		when(userService.authenticate(customer.getEmail(), testPassword)).thenReturn(customer);
		when(userService.authenticate(employee)).thenReturn(employee);
		when(userService.authenticate(customer)).thenReturn(customer);
		
		
		return userService;
	}
	
}
