package com.github.emailtohl.building.site.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.ApplicationHandleHistoryRepository;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ApplicationFormService;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class ApplicationFormServiceImplTest {
	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject ApplicationFormService applicationFormService;
	@Inject @Named("userServiceImpl") UserService userService;
	@Inject SecurityContextManager securityContextManager;
	private final String title = "test";
	private Long id;
	private Pageable pageable = new PageRequest(0, 20);
	
	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		User c = userService.getUserByEmail(PersistenceData.emailtohl.getEmail());
		ApplicationForm af = new ApplicationForm(c, title, "test content");
		applicationFormService.application(af);
		id = af.getId();
	}

	@After
	public void tearDown() throws Exception {
		if (id != null) {
			ApplicationForm af = applicationFormRepository.getOne(id);
			Long historyId = af.getApplicationHandleHistory().iterator().next().getId();
			applicationHandleHistoryRepository.delete(historyId);
			applicationFormRepository.delete(af);
		}
	}

	@Test
	public void testFindById() {
		if (id != null) {
			ApplicationForm e = applicationFormService.findById(id);
			assertNotNull(e);
		}
	}

	@Test
	public void testFindByNameLike() {
		Page<ApplicationForm> page = applicationFormService.findByNameLike(title.substring(0, title.length() - 1) + '%', pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testFindByStatus() {
		Page<ApplicationForm> page = applicationFormService.findByStatus(Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testFindByApplicantEmailLike() {
		Page<ApplicationForm> page = applicationFormService.findByApplicantEmailLike(pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testTransit() {
		String cause = "缘由是：……";
		if (id != null) {
			applicationFormService.transit(id, Status.REJECT, cause);
			ApplicationForm af = applicationFormService.findById(id);
			assertEquals(Status.REJECT, af.getStatus());
			assertEquals(cause, af.getCause());
			ApplicationHandleHistory history = af.getApplicationHandleHistory().iterator().next();
			assertNotNull(history);
			
		} else {
			fail("没有持久化");
		}
	}

	@Test
	public void testHistoryFindByCreateDateBetween() {
		fail("Not yet implemented");
	}

	@Test
	public void testHistoryFindByCreateDateGreaterThanEqual() {
		fail("Not yet implemented");
	}

	@Test
	public void testHistoryFindByCreateDateLessThanEqual() {
		fail("Not yet implemented");
	}

	@Test
	public void testHistoryFindByHandlerEmailLike() {
		fail("Not yet implemented");
	}

	@Test
	public void testHistoryFindByStatus() {
		fail("Not yet implemented");
	}

}
