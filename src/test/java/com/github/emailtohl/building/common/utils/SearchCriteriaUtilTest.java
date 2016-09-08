package com.github.emailtohl.building.common.utils;

import static org.junit.Assert.assertFalse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriteriaQueries;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion;
import com.github.emailtohl.building.initdb.PersistenceData;

public class SearchCriteriaUtilTest {
	private static final Logger logger = LogManager.getLogger();

	@Test
	public void testGet() {
		CriteriaQueries sc = SearchCriteriaUtil.get(PersistenceData.foo);
		assertFalse(sc.isEmpty());
		for (Criterion c : sc) {
			logger.debug(c);
		}
	}

}
