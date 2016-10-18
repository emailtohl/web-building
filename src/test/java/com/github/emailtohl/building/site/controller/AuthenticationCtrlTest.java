package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.mail.EmailService;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;
import com.google.gson.Gson;

public class AuthenticationCtrlTest {
	MockMvc mockMvc;
	
	@Before
	public void setUp() {
		UserDto fooDto = new UserDto();
		BeanUtils.copyProperties(foo, fooDto);
		
		AuthenticationService authenticationService = mock(AuthenticationService.class);
		when(authenticationService.authenticate("foo@test.com", "123456")).thenReturn(new Authentication() {
			private static final long serialVersionUID = -7421487535149159420L;
			@Override
			public String getName() {
				return null;
			}
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
			@Override
			public Object getCredentials() {
				return null;
			}
			@Override
			public Object getDetails() {
				return null;
			}
			@Override
			public Object getPrincipal() {
				return null;
			}
			@Override
			public boolean isAuthenticated() {
				return false;
			}
			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			}}
		);
		when(authenticationService.getPageByRoles(fooDto, new PageRequest(0, 10))).thenReturn(new Pager<UserDto>(Arrays.asList(fooDto)));
		when(authenticationService.isExist(foo.getEmail())).thenReturn(true);
		
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

		doAnswer(answer).when(authenticationService).grantedRoles(100L, new HashSet<String>(Arrays.asList(Role.MANAGER)));
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
		
		
		UserService userService = mock(UserService.class);
		when(userService.addUser(fooDto)).thenReturn(100L);
		doAnswer(answer).when(userService).enableUser(100L);
		
		EmailService emailService = mock(EmailService.class);
		doAnswer(answer).when(emailService).sendMail(foo.getEmail(), "test", "test");
		doAnswer(answer).when(emailService).enableUser("http://localhost:8080/building/register", "emailtohl@163.com");
		doAnswer(answer).when(emailService).updatePassword("http://localhost:8080/building/register", "emailtohl@163.com", "test", "test");
		
		AuthenticationCtrl authenticationCtrl = new AuthenticationCtrl();
		authenticationCtrl.setAuthenticationService(authenticationService);
		authenticationCtrl.setUserService(userService);
		authenticationCtrl.setEmailService(emailService);
		authenticationCtrl.setTaskScheduler(Spring.context.getBean(ThreadPoolTaskScheduler.class));
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
	
	@Test
	public void testForgetPassword() throws Exception {
		mockMvc.perform(post("/forgetPassword")
				.param("email", "abc")
				.param("_csrf", "_csrf")
				)
		.andExpect(status().is(404));
		mockMvc.perform(post("/forgetPassword")
				.param("email", foo.getEmail())
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
	public void testAuthorize() throws Exception {
		String authorities = new Gson().toJson(Arrays.asList(Role.MANAGER));
		mockMvc.perform(put("/authentication/authorize/100")
				.characterEncoding("UTF-8")  
		        .contentType(MediaType.APPLICATION_JSON)  
		        .content(authorities.getBytes()))
		.andExpect(status().isOk());
	}

	@Test
	public void testSecurePage() throws Exception {
		mockMvc.perform(get("/secure"))
		.andExpect(status().isOk())
		.andExpect(view().name("secure"));
	}

}
