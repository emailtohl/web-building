package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.baz;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;
import javax.transaction.Transactional;

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
import com.github.emailtohl.building.site.dao.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.ApplicationHandleHistoryRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.service.ApplicationFormService;
import com.github.emailtohl.building.stub.SecurityContextManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
//ApplicationForm#getApplicationHandleHistory()使用懒加载，事务不能在service层关闭，所以在此添加上@Transactional
@Transactional
public class ApplicationFormServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject UserRepository userRepository;
	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject ApplicationFormService applicationFormService;
	@Inject SecurityContextManager securityContextManager;
	private final String title = "test";
	private Long id;
	private Pageable pageable = new PageRequest(0, 20);
	
	@Before
	public void setUp() throws Exception {
		securityContextManager.setBaz();
		Customer c = (Customer) userRepository.findByEmail(baz.getEmail());
		ApplicationForm af = new ApplicationForm();
		af.setApplicant(c);
		af.setName(title);
		af.setDescription("test content");
		applicationFormService.application(af);
		id = af.getId();
	}

	@After
	public void tearDown() throws Exception {
		securityContextManager.setBar();
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
		securityContextManager.setBar();
		if (id != null) {
			ApplicationForm e = applicationFormService.findById(id);
			assertNotNull(e);
		}
	}

	@Test
	public void testFindByNameLike() {
		securityContextManager.setBar();
		Page<ApplicationForm> page = applicationFormService.findByNameLike(title.substring(0, title.length() - 1) + '%', pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testFindByStatus() {
		securityContextManager.setBar();
		Page<ApplicationForm> page = applicationFormService.findByStatus(Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testFindMyApplicationForm() {
		securityContextManager.setBaz();
		Page<ApplicationForm> page = applicationFormService.findMyApplicationForm(pageable);
		assertTrue(page.getTotalElements() > 0);
	}
	
	@Test
	public void testFindByNameLikeAndStatus() {
		securityContextManager.setBaz();
		Page<ApplicationForm> page = applicationFormService.findByNameAndStatus(title, Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormService.findByNameAndStatus(null, Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormService.findByNameAndStatus(title, null, pageable);
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormService.findByNameAndStatus(null, null, pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testTransit() {
		securityContextManager.setBar();
		String cause = "缘由是：……";
		if (id != null) {
			applicationFormService.transit(id, Status.REJECT, cause);
			ApplicationForm af = applicationFormService.findById(id);
			assertEquals(Status.REJECT, af.getStatus());
			assertEquals(cause, af.getCause());
			ApplicationHandleHistory history = af.getApplicationHandleHistory().iterator().next();
			assertNotNull(history);
			
			Instant now = Instant.now();
			Date start = Date.from(now.minusSeconds(1000));
			Date end = Date.from(now.plusSeconds(100));
			Page<ApplicationHandleHistory> page = applicationFormService.historyFindByCreateDateBetween(start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByCreateDateGreaterThanEqual(start, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByCreateDateLessThanEqual(end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByHandlerEmailLike(bar.getEmail(), pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByStatus(Status.REJECT, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.history(baz.getEmail(), null, Status.REJECT, start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			page = applicationFormService.history(null, bar.getEmail(), Status.REJECT, start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			page = applicationFormService.history("", "", Status.REJECT, start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
		} else {
			fail("没有持久化");
		}
	}

}
