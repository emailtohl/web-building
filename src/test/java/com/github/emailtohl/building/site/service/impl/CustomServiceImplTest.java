package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.initdb.PersistenceData.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;
import com.github.emailtohl.building.site.entities.user.Customer;
import com.github.emailtohl.building.site.entities.user.Subsidiary;
import com.github.emailtohl.building.site.entities.user.User.Gender;
import com.github.emailtohl.building.site.service.CustomerService;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
/**
 * 客户管理服务的测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class CustomServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject SecurityContextManager securityContextManager;
	@Inject CustomerService customService;
	@Inject @Named("userServiceImpl") UserService userService;
	@Inject CleanAuditData cleanAuditData;
	
	Customer cus = null;
	Long id = null;
	String name = "客户某某";
	String title = "经理";
	String affiliation = "某某有限责任公司";

	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		cus = new Customer();
		cus.setAddress("四川路");
		cus.setAge(20);
		cus.getRoles().add(user);
		user.getUsers().add(cus);
		cus.setBirthday(Date.from(Instant.now().minus(Duration.ofDays(10000))));
		cus.setDescription("test");
		cus.setEmail("xxxyyyzzz@test.com");
		cus.setPassword("1234567890");
		cus.setName(name);
		cus.setTelephone("123456789");
		cus.setUsername("username");
		cus.setGender(Gender.FEMALE);
		Subsidiary cc = new Subsidiary();
		cc.setCity("成都");
		cc.setCountry("中国");
		cc.setLanguage("zh");
		cc.setProvince("四川");
		cus.setSubsidiary(cc);
		cus.setTitle(title);
		cus.setAffiliation(affiliation);
		
		id = userService.addCustomer(cus);
	}

	@After
	public void tearDown() throws Exception {
		userService.deleteUser(id);
		cleanAuditData.cleanUserAudit(id);
		cus = null;
	}

	@Test
	public void testQuery() {
		Pageable pageable = new PageRequest(0, 20, Direction.DESC, "name", "title", "affiliation");
		Pager<Customer> p = customService.query(name, title, affiliation, pageable);
		assertTrue(p.getTotalElements() > 0);
	}

	@Test
	public void testGetCustomer() {
		assertNotNull(customService.getCustomer(id));
	}

	@Test
	public void testUpdate() {
		String name = "甲乙丙", title = "总监", affiliation = "某某科技公司";
		Customer form = new Customer();
		form.setName(name);
		form.setTitle(title);
		form.setAffiliation(affiliation);
		customService.update(id, form);
		Customer c = customService.getCustomer(id);
		assertEquals(name, c.getName());
		assertEquals(title, c.getTitle());
		assertEquals(affiliation, c.getAffiliation());
	}
	
	@Test
	public void testGetCustomerExcel() {
		Workbook wb = customService.getCustomerExcel();
		Sheet sheet = wb.getSheetAt(0);
		int firstRow = sheet.getFirstRowNum(), lastRow = sheet.getLastRowNum();
		for (int i = firstRow; i <= lastRow; i++) {
			Row r = sheet.getRow(i);
			short firstCell = r.getFirstCellNum(), lastCell = r.getLastCellNum();
			for (int j = firstCell; j <= lastCell; j++) {
				System.out.println(r.getCell(j));
			}
		}
		
	}

}
