package com.github.emailtohl.building.site.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.github.emailtohl.building.site.service.ApplicationFormService;
import com.github.emailtohl.building.stub.SecurityContextManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class ApplicationFormServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject ApplicationFormService applicationFormService;
	@Inject SecurityContextManager securityContextManager;
	private final String title = "test";
	private Long id;
	private Pageable pageable = new PageRequest(0, 20);
	
	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		ApplicationForm af = new ApplicationForm();
		af.setName(title);
		af.setDescription("test content");
		applicationFormService.application(af);
		id = af.getId();
	}

	@After
	public void tearDown() throws Exception {
		if (id != null) {
			Page<ApplicationHandleHistory> page = applicationHandleHistoryRepository.findByApplicationFormId(id, pageable);
			for (Iterator<ApplicationHandleHistory> i = page.getContent().iterator(); i.hasNext();) {
				ApplicationHandleHistory h = i.next();
				logger.debug(h);
				applicationHandleHistoryRepository.delete(h.getId());
			}
			applicationFormRepository.delete(id);
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
//			懒加载原因，session已关，不能调用
//			ApplicationHandleHistory history = af.getApplicationHandleHistory().iterator().next();
//			assertNotNull(history);
			
			Instant now = Instant.now();
			Date start = Date.from(now.minusSeconds(1000));
			Date end = Date.from(now.plusSeconds(10));
			Page<ApplicationHandleHistory> page = applicationFormService.historyFindByCreateDateBetween(start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByCreateDateGreaterThanEqual(start, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByCreateDateLessThanEqual(end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByHandlerEmailLike(PersistenceData.emailtohl.getEmail(), pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByStatus(Status.REJECT, pageable);
			assertTrue(page.getTotalElements() > 0);
		} else {
			fail("没有持久化");
		}
	}


}
