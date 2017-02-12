package com.github.emailtohl.building.site.controller;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.github.emailtohl.building.site.service.flow.ApplicationFormService;
import com.github.emailtohl.building.site.service.user.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
import com.google.gson.Gson;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class ApplicationFormCtrlTest {
	@Inject ServiceStub serviceStub;
	@Inject @Named("userServiceMock") UserService userService;
	@Inject SecurityContextManager securityContextManager;
	
	@Inject Gson gson;
	
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    UserDto empDto = new UserDto(), cusDto = new UserDto();

	@Before
	public void setUp() throws Exception {
		BeanUtils.copyProperties(serviceStub.employee, empDto);
		BeanUtils.copyProperties(serviceStub.customer, cusDto);
		securityContextManager.setBaz();
		
		ApplicationFormService applicationFormService = serviceStub.getApplicationFormService();
		
		ApplicationFormCtrl ctrl = new ApplicationFormCtrl();
		ctrl.setApplicationFormService(applicationFormService);
		mockMvc = standaloneSetup(ctrl).build();
	}

//	@Test
	public void testFindMyApplicationFormPageable() {
		fail("Not yet implemented");
	}

//	@Test
	public void testFindMyApplicationFormStringStatusPageable() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() throws Exception {
		mockMvc.perform(get("/applicationForm/" + serviceStub.applicationFormId))
		.andExpect(status().isOk());
	}

	@Test
	public void testAdd() throws Exception {
		mockMvc.perform(post("/applicationForm")
				.characterEncoding("UTF-8")
		        .contentType(MediaType.APPLICATION_JSON)  
		        .content(gson.toJson(serviceStub.applicationForm).getBytes()))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}

	@Test
	public void testTransit() throws Exception {
		serviceStub.applicationForm.setCause("处理意见……");
		mockMvc.perform(put("/applicationForm/" + serviceStub.customerId)
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(serviceStub.applicationForm).getBytes()))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	public void testGetHistoryById() throws Exception {
		mockMvc.perform(get("/applicationForm/history/" + serviceStub.historyId))
		.andExpect(status().isOk());
	}
	
//	@Test
	public void testHistory() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() throws Exception {
		mockMvc.perform(delete("/applicationForm/" + serviceStub.applicationFormId))
		.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}
}
