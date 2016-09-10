package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.initdb.PersistenceData.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;
import com.google.gson.Gson;

public class UserCtrlTest {
	Gson gson = new Gson();
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();  
    MockHttpServletResponse response = new MockHttpServletResponse(); 
	
	@Before
	public void setUp() {
		UserService userService = mock(UserService.class);
		/*Answer<Object> answer = new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return "called with arguments: " + args;
			}
		};*/
		Answer<Object> answer = invocation -> {
			Object[] args = invocation.getArguments();
			return "called with arguments: " + args;
		};

		when(userService.addUser(foo)).thenReturn(100L);
		doAnswer(answer).when(userService).enableUser(100L);
		doAnswer(answer).when(userService).disableUser(100L);
		doAnswer(answer).when(userService).changePassword("foo@test.com", "123456");
		doAnswer(answer).when(userService).deleteUser(100L);
		when(userService.getUser(100L)).thenReturn(foo);
		when(userService.getUserByEmail("foo@test.com")).thenReturn(foo);
		doAnswer(answer).when(userService).mergeUser(100L, foo);
		Pager<User> pager = new Pager<User>(Arrays.asList(foo));
		when(userService.getUserPager(foo, new PageRequest(0, 20))).thenReturn(pager);
		Page<User> page = new PageImpl<User>(Arrays.asList(foo));
		when(userService.getUserPage(foo, new PageRequest(0, 20))).thenReturn(page);
	
		UserCtrl userCtrl = new UserCtrl();
		userCtrl.setGson(gson);
		userCtrl.setUserService(userService);
		mockMvc = standaloneSetup(userCtrl).build();
	
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
