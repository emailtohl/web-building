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
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm;
import com.github.emailtohl.building.site.entities.flow.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.entities.user.Customer;
import com.github.emailtohl.building.site.entities.user.Employee;
import com.github.emailtohl.building.site.entities.user.User;
import com.github.emailtohl.building.site.service.flow.ApplicationFormService;
import com.github.emailtohl.building.site.service.user.UserService;
/**
 * 为项目测试提供统一的桩支持
 * @author HeLei
 * @date 2017.02.04
 */
public class ServiceStub {
	private static final Logger logger = LogManager.getLogger();
	public final Employee employee = new Employee();
	public final long employeeId = 200L;
	public final Customer customer = new Customer();
	public final long customerId = 100L;
	public final String testPassword = "123456";
	public final Pageable pageable = new PageRequest(0, 20);
	
	public UserService getUserService() {
		BeanUtils.copyProperties(foo, employee);
		employee.setId(employeeId);
		BeanUtils.copyProperties(baz, customer);
		customer.setId(customerId);
		
		UserService userService = mock(UserService.class);
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		when(userService.addEmployee(employee)).thenReturn(employee);
		when(userService.addCustomer(customer)).thenReturn(customer);
		when(userService.enableUser(employeeId)).thenReturn(employee);
		when(userService.enableUser(customerId)).thenReturn(customer);
		when(userService.disableUser(employeeId)).thenReturn(employee);
		when(userService.disableUser(customerId)).thenReturn(customer);
		when(userService.grantRoles(employeeId)).thenReturn(employee);
		when(userService.grantRoles(employeeId, Role.MANAGER, Role.EMPLOYEE)).thenReturn(employee);
		when(userService.grantRoles(customerId)).thenReturn(customer);
		when(userService.grantUserRole(customerId)).thenReturn(customer);
		when(userService.changePassword(employee.getEmail(), testPassword)).thenReturn(employee);
		when(userService.changePassword(customer.getEmail(), testPassword)).thenReturn(customer);
		when(userService.changePasswordByEmail(employee.getEmail(), testPassword)).thenReturn(employee);
		when(userService.changePasswordByEmail(customer.getEmail(), testPassword)).thenReturn(customer);
		doAnswer(answer).when(userService).deleteUser(employeeId);
		doAnswer(answer).when(userService).deleteUser(customerId);
		when(userService.setPublicKey("eyJtb2R1bGUiOiI5MTIxMzkwMjU2NDM1ODA1MjM4NDg4MDI5NDE3MjgxMzIxNjk4NTYxMDk2NTcwNTE5NDc2OTM4NDQ4NDA1NzgxMjAyMDM4NzM1NzQwNDg0OTczODQ5NzU2MTIzNjE3MjQ1MzI1MzMzMTEzNDMwMzAwMjc4NjIyNjc2NjkwMDEzMzkxOTgxMjAyMTk2NzY5Mjg2MDc3NzMwODkwOTkxODIyMDMzNTk4NjQ1NjkwMzU1NzYxNTU3NjUwNjkwMzI1MTE2NTUzODQ3OTI0NTc5OTk1MTQwNDM0NDkyOTk3NDg0MDg1NjM5ODI2NjU4NzY1NDM4NTE3ODk0Mzg5NTc4NDg1ODYxNDMxMjY3Mzg0OTM3MDE1MzgyMjg2MzAzODYxOTU5NzcyOTA1OTQwNDUzNjMxNjA2OSIsInB1YmxpY0tleSI6IjY1NTM3In0=")).thenReturn(employee);
		when(userService.clearPublicKey()).thenReturn(employee);
		when(userService.getUser(employeeId)).thenReturn(employee);
		when(userService.getUser(customerId)).thenReturn(customer);
		when(userService.getUserByEmail(emailtohl.getEmail())).thenReturn(emailtohl);
		when(userService.getUserByEmail(employee.getEmail())).thenReturn(customer);
		when(userService.getUserByEmail(customer.getEmail())).thenReturn(customer);
		when(userService.mergeEmployee(employee.getEmail(), employee)).thenReturn(employee);
		when(userService.mergeCustomer(customer.getEmail(), customer)).thenReturn(customer);
		Pager<User> employeePager = new Pager<User>(Arrays.asList(employee));
		Pager<User> customerPager = new Pager<User>(Arrays.asList(customer));
		when(userService.updateIconSrc(employeeId, "url")).thenReturn(employee);
		when(userService.updateIcon(employeeId, new byte[1])).thenReturn(employee);
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
