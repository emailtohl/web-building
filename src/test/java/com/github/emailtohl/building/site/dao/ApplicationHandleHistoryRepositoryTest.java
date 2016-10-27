package com.github.emailtohl.building.site.dao;

import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
@Transactional
public class ApplicationHandleHistoryRepositoryTest {
	static final Logger logger = LogManager.getLogger();
	@Inject ApplicationHandleHistoryRepository dao;
	@Inject UserRepository userRepository;
	PageRequest pageable = new PageRequest(0, 20);
	Long id;
	
	@Before
	public void setUp() throws Exception {
		User applicat = userRepository.findByEmail(PersistenceData.emailtohl.getEmail());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHistory1() {
		fail("Not yet implemented");
	}

	@Test
	public void testHistory2() {
		fail("Not yet implemented");
	}

	@Test
	public void testHistory3() {
		fail("Not yet implemented");
	}

}
