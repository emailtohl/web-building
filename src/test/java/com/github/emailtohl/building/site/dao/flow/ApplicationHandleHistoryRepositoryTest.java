package com.github.emailtohl.building.site.dao.flow;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

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
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.flow.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.flow.ApplicationHandleHistoryRepository;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm;
import com.github.emailtohl.building.site.entities.flow.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.user.Customer;
import com.github.emailtohl.building.site.entities.user.Employee;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
@Transactional
public class ApplicationHandleHistoryRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject UserRepository userRepository;
	String title = "工单申请表";
	Pageable pageable = new PageRequest(0, 20);
	Long formId;
	Long historyId;
	
	@Before
	public void setUp() {
		Customer c = (Customer) userRepository.findByEmail(PersistenceData.baz.getEmail());
		Employee e = (Employee) userRepository.findByEmail(PersistenceData.bar.getEmail());
		ApplicationForm af = new ApplicationForm(c, title, "工单申请的内容");
		applicationFormRepository.save(af);
		formId = af.getId();
		
		ApplicationHandleHistory h = new ApplicationHandleHistory();
		h.setApplicationForm(af);
		h.setHandler(e);
		h.setStatus(Status.PROCESSING);
		h.setCause("处理中……");
		
		af.setHandler(e);
		af.setCause("处理中……");
		af.setStatus(Status.PROCESSING);
		
		applicationHandleHistoryRepository.save(h);
		historyId = h.getId();
	}
	
	@After
	public void tearDown() throws Exception {
		if (historyId != null)
			applicationHandleHistoryRepository.delete(historyId);
		if (formId != null)
			applicationFormRepository.delete(formId);
	}
	
	@Test
	public void test() {
		Instant now = Instant.now();
		Date start = Date.from(now.minus(3650, ChronoUnit.DAYS));
		Date end = Date.from(now.plus(3650, ChronoUnit.DAYS));
		Page<ApplicationHandleHistory> page = applicationHandleHistoryRepository.history1(PersistenceData.baz.getEmail(), Status.PROCESSING, start, end, pageable);
		logger.debug(page.getContent());
		logger.debug(page.getTotalElements());
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationHandleHistoryRepository.history2(PersistenceData.bar.getEmail(), Status.PROCESSING, start, end, pageable);
		logger.debug(page.getContent());
		logger.debug(page.getTotalElements());
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationHandleHistoryRepository.history3(PersistenceData.baz.getEmail(), PersistenceData.bar.getEmail(), Status.PROCESSING, start, end, pageable);
		logger.debug(page.getContent());
		logger.debug(page.getTotalElements());
		assertTrue(page.getTotalElements() > 0);
		
		List<ApplicationHandleHistory> ls = applicationHandleHistoryRepository.findByApplicationFormIdWhenException(formId);
		logger.debug(ls);
		
	}

}
