package com.github.emailtohl.building.site.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.github.emailtohl.building.site.entities.flow.ApplicationForm;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.user.Customer;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
@Transactional
public class ApplicationFormRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject UserRepository userRepository;
	
	@Test
	public void test() {
		String title = "工单申请表";
		Pageable p = new PageRequest(0, 20);
		Customer c = (Customer) userRepository.findByEmail(PersistenceData.baz.getEmail());
		ApplicationForm persist = new ApplicationForm(c, title, "工单申请的内容");
		applicationFormRepository.save(persist);
		Long id = persist.getId();
		ApplicationForm af = applicationFormRepository.findOne(id);
		logger.debug(af);
		assertNotNull(af);
		Page<ApplicationForm> page = applicationFormRepository.findByNameLike(title.substring(0, title.length() - 1) + '%', p);
		logger.debug(page.getContent());
		assertTrue(page.getTotalElements() > 0);
		page = applicationFormRepository.findByStatus(Status.REQUEST, p);
		logger.debug(page.getContent());
		assertTrue(page.getTotalElements() > 0);
		page = applicationFormRepository.findByApplicantEmailLike(PersistenceData.baz.getEmail(), p);
		logger.debug(page.getContent());
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormRepository.findByNameLikeAndStatus(title, Status.REQUEST, p);
		assertTrue(page.getTotalElements() > 0);
		
		applicationFormRepository.delete(persist);
	}

}
