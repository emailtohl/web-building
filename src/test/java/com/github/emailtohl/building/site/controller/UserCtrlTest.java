package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.service.UserService;
import com.google.gson.Gson;

public class UserCtrlTest {
	Gson gson = new Gson();
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    UserDto fooDto = new UserDto();
	
	@Before
	public void setUp() {
		BeanUtils.copyProperties(fooDto, fooDto);
		
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

		when(userService.addUser(fooDto)).thenReturn(100L);
		doAnswer(answer).when(userService).enableUser(100L);
		doAnswer(answer).when(userService).disableUser(100L);
		doAnswer(answer).when(userService).changePassword("foo@test.com", "123456");
		doAnswer(answer).when(userService).deleteUser(100L);
		when(userService.getUser(100L)).thenReturn(fooDto);
		when(userService.getUserByEmail("foo@test.com")).thenReturn(fooDto);
		doAnswer(answer).when(userService).mergeUser(100L, fooDto);
		Pager<UserDto> pager = new Pager<UserDto>(Arrays.asList(fooDto));
		when(userService.getUserPager(fooDto, new PageRequest(0, 20))).thenReturn(pager);
		Page<UserDto> page = new PageImpl<UserDto>(Arrays.asList(fooDto));
		when(userService.getUserPage(fooDto, new PageRequest(0, 20))).thenReturn(page);
	
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
