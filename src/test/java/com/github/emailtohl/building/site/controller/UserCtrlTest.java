package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class UserCtrlTest {
	@Inject ServiceStub serviceStub;
	@Inject @Named("userServiceMock") UserService userService;
	@Inject SecurityContextManager securityContextManager;
	
	Gson gson = new Gson();
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    UserDto fooDto = new UserDto();
	
	@Before
	public void setUp() {
		BeanUtils.copyProperties(foo, fooDto);
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
		mockMvc.perform(options("/user/100"))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()))
		.andExpect(header().stringValues("Allow", "OPTIONS,HEAD,GET,PUT,DELETE"));
	}

	@Test
	public void testGetUserById() throws Exception {
		mockMvc.perform(get("/user/id/100"))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/user/id/0"))
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	public void testGetUserByEmail() throws Exception {
		mockMvc.perform(get("/user/email?email=foo@test.com"))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/user/email?email=bar@test.com"))
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

//	@Test
	public void testGetUserPager() throws Exception {
		mockMvc.perform(get("/user/pager?email=foo@test.com&page=0&size=20"))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/user/email?email=bar@test.com&page=0&size=20"))
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	public void testAddUser() throws Exception {
		mockMvc.perform(post("/user")
		.characterEncoding("UTF-8")
        .contentType(MediaType.APPLICATION_JSON)  
        .content(gson.toJson(foo).getBytes()))
		.andExpect(status().is(HttpStatus.CREATED.value()));
		
		mockMvc.perform(post("/user")
		.characterEncoding("UTF-8")  
        .contentType(MediaType.APPLICATION_JSON)  
        .content("{username:foo}".getBytes()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void testEnableUser() throws Exception {
		mockMvc.perform(put("/user/enableUser/100")
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testDisableUser() throws Exception {
		mockMvc.perform(put("/user/disableUser/100")
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdate() throws Exception {
		mockMvc.perform(put("/user/100")
		.characterEncoding("UTF-8")
        .contentType(MediaType.APPLICATION_JSON)  
        .content(gson.toJson(foo).getBytes()))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
		
		mockMvc.perform(put("/user/100")
		.characterEncoding("UTF-8")  
        .contentType(MediaType.APPLICATION_JSON)  
        .content("{username:foo}".getBytes()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void testDelete() throws Exception {
		mockMvc.perform(delete("/user/100"))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

}
