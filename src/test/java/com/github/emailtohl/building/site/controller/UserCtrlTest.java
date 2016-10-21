package com.github.emailtohl.building.site.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class UserCtrlTest {
	@Inject ServiceStub serviceStub;
	@Inject @Named("userServiceMock") UserService userService;
	@Inject SecurityContextManager securityContextManager;
	
	@Inject Gson gson;
	
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    UserDto empDto = new UserDto(), cusDto = new UserDto();
	
	@Before
	public void setUp() {
		UserCtrl userCtrl = new UserCtrl();
		userCtrl.setGson(gson);
		userCtrl.setUserService(userService);
		mockMvc = standaloneSetup(userCtrl).build();
		
		BeanUtils.copyProperties(serviceStub.employee, empDto);
		BeanUtils.copyProperties(serviceStub.customer, cusDto);
		securityContextManager.setEmailtohl();
	}
	
	@Test
	public void testDiscover() throws Exception {
		mockMvc.perform(options("/user"))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()))
		.andExpect(header().stringValues("Allow", "OPTIONS,HEAD,GET"));
	}

	@Test
	public void testDiscoverLong() throws Exception {
		mockMvc.perform(options("/user/" + serviceStub.customerId))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()))
		.andExpect(header().stringValues("Allow", "OPTIONS,HEAD,GET,PUT,DELETE"));
	}

	@Test
	public void testGetUserById() throws Exception {
		mockMvc.perform(get("/user/id/" + serviceStub.customerId))
		.andExpect(status().isOk());
		/* id为0会触发service层的约束异常，大于0不能确定是否存在User
		mockMvc.perform(get("/user/id/0"))
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()));*/
	}

	@Test
	public void testGetUserByEmail() throws Exception {
		mockMvc.perform(get("/user/email?email=" + empDto.getEmail()))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/user/email?email=aaa@test.com"))
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
//	@Test
	public void testGetPageByRoles() throws Exception {
		mockMvc.perform(get("/user/pageByRoles?email=" + empDto.getEmail()))
		.andExpect(status().isOk());
	}

//	@Test
	public void testGetUserPager() throws Exception {
		mockMvc.perform(get("/user/pager?email=foo@test.com&page=0&size=20"))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/user/email?email=bar@test.com&page=0&size=20"))
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	public void testAddEmployee() throws Exception {
		mockMvc.perform(post("/user/employee")
		.characterEncoding("UTF-8")
        .contentType(MediaType.APPLICATION_JSON)  
        .content(gson.toJson(empDto).getBytes()))
		.andExpect(status().is(HttpStatus.CREATED.value()));
		
		mockMvc.perform(post("/user/employee")
		.characterEncoding("UTF-8")  
        .contentType(MediaType.APPLICATION_JSON)  
        .content("{username:\"foo\"}".getBytes()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void testAddCustomer() throws Exception {
		mockMvc.perform(post("/user/customer")
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)  
				.content(gson.toJson(cusDto).getBytes()))
		.andExpect(status().is(HttpStatus.CREATED.value()));
		
		mockMvc.perform(post("/user/customer")
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON)  
				.content("{username:\"foo\"}".getBytes()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void testEnableUser() throws Exception {
		mockMvc.perform(put("/user/enableUser/" + serviceStub.customerId)
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testDisableUser() throws Exception {
		mockMvc.perform(put("/user/disableUser/"  + serviceStub.customerId)
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdateEmployee() throws Exception {
		mockMvc.perform(put("/user/employee/" + serviceStub.employeeId)
		.characterEncoding("UTF-8")
        .contentType(MediaType.APPLICATION_JSON)  
        .content(gson.toJson(empDto).getBytes()))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
		
		mockMvc.perform(put("/user/employee/" + serviceStub.employeeId)
		.characterEncoding("UTF-8")  
        .contentType(MediaType.APPLICATION_JSON)  
        .content("{username:\"foo\"}".getBytes()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void testUpdateCustomer() throws Exception {
		mockMvc.perform(put("/user/customer/" + serviceStub.customerId)
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_JSON)  
				.content(gson.toJson(cusDto).getBytes()))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
		
		mockMvc.perform(put("/user/customer/" + serviceStub.customerId)
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON)  
				.content("{username:\"baz\"}".getBytes()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void testDelete() throws Exception {
		mockMvc.perform(delete("/user/" + serviceStub.customerId))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

}
