package com.github.emailtohl.building.site.dao.impl;

import static com.github.emailtohl.building.initdb.PersistenceData.qux;
import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.CustomerRepository;
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
public class CustomerRepositoryImplTest {
	@Inject CustomerRepository customerRepository;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQuery() {
		Page<Customer> p = customerRepository.query(qux.getName(), qux.getTitle(), qux.getAffiliation(), new PageRequest(0, 20));
		assertFalse(p.getContent().isEmpty());
	}

	@Test
	public void testFindAll() {
		List<Customer> ls = customerRepository.findAll();
		assertFalse(ls.isEmpty());
	}

}
