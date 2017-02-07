package com.github.emailtohl.building.site.controller;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.github.emailtohl.building.site.dto.ForumPostDto;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.service.ForumPostService;
import com.google.gson.Gson;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
public class ForumPostControllerTest {
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    Pageable pageable = new PageRequest(0, 20);
    
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() {
		ForumPostService forumPostService = mock(ForumPostService.class);
		Answer<Object> answer = invocation -> {
			Object[] args = invocation.getArguments();
			return "called with arguments: " + args;
		};
		when(forumPostService.search(null, pageable)).thenReturn(null);
		when(forumPostService.getPager(pageable)).thenReturn(null);
		when(forumPostService.findForumPostByTitle(null)).thenReturn(null);
		doAnswer(answer).when(forumPostService).save(null, null, null, null);
		doAnswer(answer).when(forumPostService).delete(100L);
		ForumPostCtrl ctrl = new ForumPostCtrl();
		ctrl.setForumPostService(forumPostService);
		mockMvc = standaloneSetup(ctrl).build();
	}
	@Test
	public void testAdd() throws Exception {
		UserDto u = new UserDto();
		u.setEmail("foo@test.com");
		u.setName("foo");
		ForumPostDto dto = new ForumPostDto();
		dto.setEmail("foo@test.com");
		dto.setUser(u);
		dto.setTitle("hello world");
		dto.setBody("hello world");
		dto.setKeywords("hello world");
		mockMvc.perform(post("/forum")
				.characterEncoding("UTF-8")  
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(new Gson().toJson(dto)))
				.andExpect(status().isOk());
	}

//	@Test
	public void testSearch() throws Exception {
		mockMvc.perform(get("/forum/search?query=first"))
		.andExpect(status().isOk());
	}

//	@Test
	public void testGetPager() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() throws Exception {
		mockMvc.perform(delete("/forum/" + 100))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}
}
