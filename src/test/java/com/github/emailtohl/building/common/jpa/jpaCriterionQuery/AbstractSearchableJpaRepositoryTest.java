package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.AbstractCriterionQueryRepository;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriteriaQueries;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion;
import com.github.emailtohl.building.site.entities.User;

public class AbstractSearchableJpaRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	class Concrete extends AbstractCriterionQueryRepository<User> {}
	Concrete concrete;
	@Before
	public void setUp() {
		concrete = new Concrete();
		AutowireCapableBeanFactory factory = SpringUtils.context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(concrete, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(concrete, "concreteSearchableJpaRepository");
	}
	
	@Test
	public void testSearch() {
		Sort sort = new Sort(Sort.Direction.DESC, "createDate")
				 .and(new Sort(Sort.Direction.ASC, "id"));
		Pageable p = new PageRequest(0, 20, sort);
		CriteriaQueries sc = CriteriaQueries.Builder.create();
		Criterion c1, c2;
		c1 = new Criterion("email", Criterion.Operator.EQ, "emailtohl@163.com");
		c2 = new Criterion("age", Criterion.Operator.GT, 20);
		sc.add(c1);
		sc.add(c2);
		Page<User> page = concrete.search(sc, p);
		logger.debug(page.getContent());
		logger.debug(page.getNumber());
		logger.debug(page.getNumberOfElements());
		logger.debug(page.getSize());
		logger.debug(page.getTotalElements());
		logger.debug(page.getSort());
		assertFalse(page.getContent().isEmpty());
	}

}
