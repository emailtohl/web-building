package com.github.emailtohl.building.site.controller;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
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
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.service.CustomerService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.google.gson.Gson;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class CustomerCtrlTest {
	private static final Logger logger = LogManager.getLogger();
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    @Inject SecurityContextManager securityContextManager;
    @Inject Gson gson;

	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		CustomerService customerService = mock(CustomerService.class);
		when(customerService.getCustomer(100L)).thenReturn(new Customer());
		Customer form = new Customer();
		form.setName("xxx");
		form.setTitle("yyy");
		form.setAffiliation("zzz");
		doAnswer(answer).when(customerService).update(100L, form);
		CustomerCtrl ctrl = new CustomerCtrl();
		ctrl.setCustomerService(customerService);
		mockMvc = standaloneSetup(ctrl).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCustomer() throws Exception {
		mockMvc.perform(get("/customer/100"))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdate() throws Exception {
		UserDto f = new UserDto();
		f.setName("xxx");
		f.setTitle("yyy");
		f.setAffiliation("zzz");
		mockMvc.perform(put("/customer/100")
				.characterEncoding("UTF-8")  
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(f).getBytes()))
		.andExpect(status().isOk());
	}

}
