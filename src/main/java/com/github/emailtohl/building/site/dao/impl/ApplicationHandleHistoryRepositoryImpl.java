package com.github.emailtohl.building.site.dao.impl;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.AbstractCriterionQueryRepository;
import com.github.emailtohl.building.site.dao.ApplicationHandleHistoryRepositoryCustomization;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;

/**
 * 只需继承AbstractCriterionQueryRepository即可获得动态查询能力
 * 
 * @author HeLei
 */
public class ApplicationHandleHistoryRepositoryImpl extends AbstractCriterionQueryRepository<ApplicationHandleHistory>
		implements ApplicationHandleHistoryRepositoryCustomization {

}
