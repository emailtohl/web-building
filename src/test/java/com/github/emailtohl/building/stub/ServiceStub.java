package com.github.emailtohl.building.stub;

import static com.github.emailtohl.building.initdb.PersistenceData.baz;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ApplicationFormService;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 为项目测试提供统一的桩支持
 * @author HeLei
 */
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
		doAnswer(answer).when(userService).setPublicKey("eyJtb2R1bGUiOiI5MTIxMzkwMjU2NDM1ODA1MjM4NDg4MDI5NDE3MjgxMzIxNjk4NTYxMDk2NTcwNTE5NDc2OTM4NDQ4NDA1NzgxMjAyMDM4NzM1NzQwNDg0OTczODQ5NzU2MTIzNjE3MjQ1MzI1MzMzMTEzNDMwMzAwMjc4NjIyNjc2NjkwMDEzMzkxOTgxMjAyMTk2NzY5Mjg2MDc3NzMwODkwOTkxODIyMDMzNTk4NjQ1NjkwMzU1NzYxNTU3NjUwNjkwMzI1MTE2NTUzODQ3OTI0NTc5OTk1MTQwNDM0NDkyOTk3NDg0MDg1NjM5ODI2NjU4NzY1NDM4NTE3ODk0Mzg5NTc4NDg1ODYxNDMxMjY3Mzg0OTM3MDE1MzgyMjg2MzAzODYxOTU5NzcyOTA1OTQwNDUzNjMxNjA2OSIsInB1YmxpY0tleSI6IjY1NTM3In0=");
		doAnswer(answer).when(userService).clearPublicKey();
		when(userService.getUser(employeeId)).thenReturn(employee);
		when(userService.getUser(customerId)).thenReturn(customer);
		when(userService.getUserByEmail(emailtohl.getEmail())).thenReturn(emailtohl);
		when(userService.getUserByEmail(employee.getEmail())).thenReturn(customer);
		when(userService.getUserByEmail(customer.getEmail())).thenReturn(customer);
		doAnswer(answer).when(userService).mergeEmployee(employee.getEmail(), employee);
		doAnswer(answer).when(userService).mergeCustomer(customer.getEmail(), customer);
		Pager<User> employeePager = new Pager<User>(Arrays.asList(employee));
		Pager<User> customerPager = new Pager<User>(Arrays.asList(customer));
		doAnswer(answer).when(userService).updateIconSrc(employeeId, "url");
		doAnswer(answer).when(userService).updateIcon(employeeId, new byte[1]);
		when(userService.getUserPager(employee, pageable)).thenReturn(employeePager);
		when(userService.getUserPager(customer, pageable)).thenReturn(customerPager);
		Page<User> employeePage = new PageImpl<User>(Arrays.asList(employee));
		Page<User> customerPage = new PageImpl<User>(Arrays.asList(customer));
		when(userService.getUserPage(employee, pageable)).thenReturn(employeePage);
		when(userService.getUserPage(customer, pageable)).thenReturn(customerPage);
		when(userService.isExist(employee.getEmail())).thenReturn(true);
		when(userService.isExist(customer.getEmail())).thenReturn(true);
		when(userService.getPageByRoles(employee.getEmail(), employee.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()), pageable)).thenReturn(employeePager);
		when(userService.getPageByRoles(customer.getEmail(), customer.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()), pageable)).thenReturn(customerPager);
		when(userService.getRoles()).thenReturn(new ArrayList<Role>());
		when(userService.authenticate(employee.getEmail(), testPassword)).thenReturn(employee.getAuthentication());
		when(userService.authenticate(customer.getEmail(), testPassword)).thenReturn(customer.getAuthentication());
		when(userService.authenticate(employee.getAuthentication())).thenReturn(employee.getAuthentication());
		when(userService.authenticate(customer.getAuthentication())).thenReturn(customer.getAuthentication());
		
		return userService;
	}
	
	
	private Instant now = Instant.now();
	public final Date start = Date.from(now.minusSeconds(1000));
	public final Date end = Date.from(now.plusSeconds(100));
	public final Long applicationFormId = 100L;
	public final Long historyId = 100L;
	public final String applicationFormTitle = "测试申请单标题";
	public final String applicationFormDescription = "测试申请单的内容……";
	public final Status applicationFormStatus = Status.REQUEST;
	public final ApplicationForm applicationForm = new ApplicationForm(customer, applicationFormTitle, applicationFormDescription);
	public final ApplicationHandleHistory applicationHandleHistory = new ApplicationHandleHistory(applicationForm, employee, "处理意见", applicationFormStatus);
	public ApplicationFormService getApplicationFormService() {
		ApplicationFormService applicationFormService = mock(ApplicationFormService.class);
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		when(applicationFormService.application(applicationFormTitle, applicationFormDescription)).thenReturn(applicationFormId);
		when(applicationFormService.findById(applicationFormId)).thenReturn(applicationForm);
		
		Page<ApplicationForm> page = new PageImpl<ApplicationForm>(Arrays.asList(applicationForm), pageable, 1L);
		Page<ApplicationHandleHistory> historypage = new PageImpl<ApplicationHandleHistory>(Arrays.asList(applicationHandleHistory), pageable, 1L);
		
		when(applicationFormService.findByNameAndStatus(applicationFormTitle, applicationFormStatus, pageable)).thenReturn(page);
		when(applicationFormService.findByNameLike(applicationFormTitle, pageable)).thenReturn(page);
		when(applicationFormService.findByStatus(applicationFormStatus, pageable)).thenReturn(page);
		when(applicationFormService.findMyApplicationForm(pageable)).thenReturn(page);
		when(applicationFormService.getHistoryById(historyId)).thenReturn(applicationHandleHistory);
		when(applicationFormService.history(customer.getEmail(), employee.getEmail(), applicationFormTitle, applicationFormStatus, start, end, pageable)).thenReturn(historypage);
		doAnswer(answer).when(applicationFormService).transit(applicationFormId, applicationFormStatus, "test 处理意见……");
		doAnswer(answer).when(applicationFormService).delete(applicationFormId);
		
		return applicationFormService;
	}
}
