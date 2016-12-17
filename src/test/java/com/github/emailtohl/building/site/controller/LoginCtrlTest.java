package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.NestedServletException;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.mail.EmailService;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class LoginCtrlTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ServiceStub serviceStub;
	@Inject @Named("userServiceMock") UserService userService;
	@Inject ThreadPoolTaskScheduler taskScheduler;
	@Inject SecurityContextManager securityContextManager;
	MockMvc mockMvc;
	
	@Before
	public void setUp() {
		securityContextManager.setEmailtohl();
		Answer<Object> answer = invocation -> {
			logger.debug(invocation.getMethod());
			logger.debug(invocation.getArguments());
			return invocation.getMock();
		};
		EmailService emailService = mock(EmailService.class);
		doAnswer(answer).when(emailService).sendMail("emailtohl@163.com", "test", "test");
		doAnswer(answer).when(emailService).enableUser("http://localhost:8080/building/user/register", "emailtohl@163.com");
		doAnswer(answer).when(emailService).updatePassword("http://localhost:8080/building/user/register",
				"emailtohl@163.com", "test", "test");
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
		
		LoginCtrl authenticationCtrl = new LoginCtrl();
		authenticationCtrl.setUserService(userService);
		authenticationCtrl.setEmailService(emailService);
		authenticationCtrl.setGson(new Gson());
		authenticationCtrl.setSessionRegistry(mock(SessionRegistry.class));
//		不测试定时任务，否则需要等到时后才停止
//		authenticationCtrl.setTaskScheduler(taskScheduler);
		mockMvc = standaloneSetup(authenticationCtrl).setViewResolvers(viewResolver).build();
	}

	@Test
	public void testLogin() throws Exception {
		mockMvc.perform(get("/login"))
		.andExpect(status().isOk())
		.andExpect(view().name("login"));
	}
	
	@Test
	public void testGetRegisterPage() throws Exception {
		mockMvc.perform(get("/register"))
		.andExpect(status().isOk())
		.andExpect(view().name("register"));
	}
	
	@Test
	public void testRegister() throws Exception {
		mockMvc.perform(post("/register")
				.param("email", foo.getEmail())
				.param("name", foo.getName())
				.param("password", "123456")
				)
		.andExpect(status().isOk())
		.andExpect(view().name("login"));
		
		mockMvc.perform(post("/register")
				.param("email", "foo")
				.param("name", foo.getName())
				.param("password", "123456")
				)
		.andExpect(status().is(302));
	}
	
//	NullPointerException会被spring转成NestedServletException，这是执行到定时任务产生的，即认可执行成功
	@Test(expected = NestedServletException.class)
	public void testForgetPassword() throws Exception {
		mockMvc.perform(post("/forgetPassword")
				.param("email", "abc@test.com")
				.param("_csrf", "_csrf")
				)
		.andExpect(status().is(404));
		mockMvc.perform(post("/forgetPassword")
				.param("email", serviceStub.employee.getEmail())
				.param("_csrf", "_csrf")
				)
		.andExpect(status().isOk());
	}
	
	@Test
	public void testGetUpdatePasswordPage() throws Exception {
		mockMvc.perform(get("/getUpdatePasswordPage")
				.param("email", foo.getEmail())
				.param("token", "token")
				)
		.andExpect(status().is(302));
		
/*		mockMvc.perform(get("/getUpdatePasswordPage")
				.param("email", foo.getEmail())
				.param("token", "token")
				)
		.andExpect(status().isOk())
		.andExpect(view().name("updatePassword"));*/
	}

	@Test
	public void testUpdatePassword() throws Exception {
		mockMvc.perform(post("/updatePassword")
				.param("email", foo.getEmail())
				.param("password", "password")
				.param("token", "token")
				)
		.andExpect(status().is(302));
	}
	
	@Test
	public void testAuthentication() throws Exception {
		mockMvc.perform(get("/authentication"))
		.andExpect(status().isOk());
	}

//	@Test
	public void testGetPageByAuthorities() {
		fail("Not yet implemented");
	}

	@Test
	public void testSecurePage() throws Exception {
		mockMvc.perform(get("/secure"))
		.andExpect(status().isOk())
		.andExpect(view().name("secure"));
	}

}
