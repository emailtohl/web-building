package com.github.emailtohl.building.site.controller;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.service.AuditedService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class AuditCtrlTest {
	MockMvc mockMvc;
	MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    @Inject SecurityContextManager securityContextManager;
    @Inject Gson gson;
    
	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		AuditedService auditedService = mock(AuditedService.class);
		AuditCtrl ctrl = new AuditCtrl();
		ctrl.setAuditedService(auditedService);
		mockMvc = standaloneSetup(ctrl).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUserAtRevision() throws Exception {
		mockMvc.perform(get("/audited/userAtRevision?userId=1&revision=1"))
		.andExpect(status().isOk());
	}

	@Test
	public void testGetRoleAtRevision() throws Exception {
		mockMvc.perform(get("/audited/roleAtRevision?roleId=1&revision=1"))
		.andExpect(status().isOk());
	}

}
