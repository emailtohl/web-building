package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.google.gson.Gson;

public class AuthenticationCtrlTest {
	MockMvc mockMvc;
	
	@Before
	public void setUp() {
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
		when(authenticationService.getPageByAuthorities(foo, new PageRequest(0, 10))).thenReturn(new Pager<User>(Arrays.asList(foo)));
		
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

		doAnswer(answer).when(authenticationService).grantedAuthority(100L, new HashSet<Authority>(Arrays.asList(Authority.MANAGER)));
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
		
		AuthenticationCtrl authenticationCtrl = new AuthenticationCtrl();
		authenticationCtrl.setAuthenticationService(authenticationService);
		mockMvc = standaloneSetup(authenticationCtrl).setViewResolvers(viewResolver).build();
	}

	@Test
	public void testLogin() throws Exception {
		mockMvc.perform(get("/login"))
		.andExpect(status().isOk())
		.andExpect(view().name("login"));
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
		String authorities = new Gson().toJson(Arrays.asList(Authority.MANAGER));
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
