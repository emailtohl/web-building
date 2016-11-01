package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class AbstractCriterionQueryRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ApplicationContext context;
	class UserRepository extends AbstractCriterionQueryRepository<User> {}
	class EmployeeRepository extends AbstractCriterionQueryRepository<Employee> {}
	UserRepository userRepository;
	EmployeeRepository employeeRepository;
	@Before
	public void setUp() {
		userRepository = new UserRepository();
		employeeRepository = new EmployeeRepository();
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(userRepository, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(userRepository, "userForAbstractCriterionQueryRepositoryTest");
		factory.autowireBeanProperties(employeeRepository, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(employeeRepository, "employeeForAbstractCriterionQueryRepositoryTest");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUserSearch() {
		Sort sort = new Sort(Sort.Direction.DESC, "createDate")
				 .and(new Sort(Sort.Direction.ASC, "id"));
		Pageable p = new PageRequest(0, 20, sort);
		List<Criterion> sc = new ArrayList<>();
		Criterion c1, c2, c3, c4, c5, c6, c7;
		c1 = new Criterion("email", Criterion.Operator.EQ, emailtohl.getEmail());
		c2 = new Criterion("age", Criterion.Operator.GTE, 10);
		c3 = new Criterion("age", Criterion.Operator.LTE, 150);
		c4 = new Criterion("subsidiary.mobile", Criterion.Operator.NOT_NULL, null);
		c5 = new Criterion("telephone", Criterion.Operator.NULL, null);
		
		Set<Integer> in = new HashSet<>(), notIn = new HashSet<>();
		for (int i = 0; i < 150; i++) {
			in.add(i);
		}
		for (int i = -150; i < 0; i++) {
			notIn.add(i);
		}
		c6 = new Criterion("age", Criterion.Operator.IN, in);
		c7 = new Criterion("age", Criterion.Operator.NOT_IN, notIn);
		sc.add(c1);
		sc.add(c2);
		sc.add(c3);
		sc.add(c4);
		sc.add(c5);
		sc.add(c6);
		sc.add(c7);
		Page<User> page = userRepository.search(sc, p);
		logger.debug(page.getContent());
		logger.debug(page.getNumber());
		logger.debug(page.getNumberOfElements());
		logger.debug(page.getSize());
		logger.debug(page.getTotalElements());
		logger.debug(page.getSort());
		assertFalse(page.getContent().isEmpty());
		
		
		Criterion c8 = new Criterion("department.name", Criterion.Operator.LIKE, foo.getDepartment().getName());
		sc.clear();
		sc.add(c8);
		page = userRepository.search(sc, p);
	}
	
	@Test
	public void testEmployeeSearch() {
		Sort sort = new Sort(Sort.Direction.DESC, "createDate")
				 .and(new Sort(Sort.Direction.ASC, "id"));
		Pageable p = new PageRequest(0, 20, sort);
		List<Criterion> sc = new ArrayList<>();
		Criterion c1, c2, c3, c4, c5, c6;
		c1 = new Criterion("email", Criterion.Operator.EQ, foo.getEmail());
		c2 = new Criterion("department.name", Criterion.Operator.LIKE, foo.getDepartment().getName());
		c3 = new Criterion("department.company.name", Criterion.Operator.LIKE, foo.getDepartment().getCompany().getName());
		c4 = new Criterion("salary", Criterion.Operator.GT, foo.getSalary() - 1);
		c5 = new Criterion("salary", Criterion.Operator.LT, foo.getSalary() + 1);
		c6 = new Criterion("username", Criterion.Operator.NOT_LIKE, bar.getUsername());
		sc.add(c1);
		sc.add(c2);
		sc.add(c3);
		sc.add(c4);
		sc.add(c5);
		sc.add(c6);
		Page<Employee> page = employeeRepository.search(sc, p);
		logger.debug(page.getContent());
		logger.debug(page.getNumber());
		logger.debug(page.getNumberOfElements());
		logger.debug(page.getSize());
		logger.debug(page.getTotalElements());
		logger.debug(page.getSort());
		assertFalse(page.getContent().isEmpty());
	}

}
