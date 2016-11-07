package com.github.emailtohl.building.common.utils;

import static com.github.emailtohl.building.initdb.PersistenceData.*;
import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.stub.SecurityContextManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class SecurityContextUtilsTest {
	@Inject SecurityContextManager securityContextManager;
	@Inject SecurityContextUtils securityContextUtils;

	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAuthentication() {
		assertNotNull(securityContextUtils.getAuthentication());
	}

	@Test
	public void testGetCurrentUsername() {
		assertEquals(emailtohl.getEmail(), securityContextUtils.getCurrentUsername());
	}

	@Test
	public void testGetAuthorities() {
		assertTrue(securityContextUtils.getAuthorities().contains(user_delete.getName()));
	}

	@Test
	public void testHasAnyAuthority() {
		assertTrue(securityContextUtils.hasAnyAuthority(user_delete.getName(), user_disable.getName(), user_grant_roles.getName()));
	}

}