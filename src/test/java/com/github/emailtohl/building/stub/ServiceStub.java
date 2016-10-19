package com.github.emailtohl.building.stub;

import static com.github.emailtohl.building.initdb.PersistenceData.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 为项目测试提供统一的桩支持
 * @author HeLei
 */
@SuppressWarnings("unused")
public class ServiceStub {
	private static final Logger logger = LogManager.getLogger();
	public final User testUserDto = baz;
	public final long testId = 100L;
	public final String testPassword = "123456";
	
	public UserService getUserService() {
		UserService userService = mock(UserService.class);
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		when(userService.addEmployee(foo)).thenReturn(testId);
		when(userService.addCustomer(baz)).thenReturn(testId);
		doAnswer(answer).when(userService).enableUser(testId);
		doAnswer(answer).when(userService).disableUser(testId);
		doAnswer(answer).when(userService).grantRoles(testId);
		doAnswer(answer).when(userService).changePassword(testUserDto.getEmail(), testPassword);
		doAnswer(answer).when(userService).changePasswordByEmail(testUserDto.getEmail(), testPassword);
		doAnswer(answer).when(userService).deleteUser(testId);
		when(userService.getUser(testId)).thenReturn(testUserDto);
		when(userService.getUserByEmail(testUserDto.getEmail())).thenReturn(testUserDto);
		doAnswer(answer).when(userService).mergeUser(testUserDto.getEmail(), testUserDto);
		Pager<User> pager = new Pager<User>(Arrays.asList(testUserDto));
		when(userService.getUserPager(testUserDto, new PageRequest(0, 20))).thenReturn(pager);
		Page<User> page = new PageImpl<User>(Arrays.asList(testUserDto));
		when(userService.getUserPage(testUserDto, new PageRequest(0, 20))).thenReturn(page);
		return userService;
	}
	
}
