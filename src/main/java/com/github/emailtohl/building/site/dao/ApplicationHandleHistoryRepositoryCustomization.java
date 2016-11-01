package com.github.emailtohl.building.site.dao;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriterionQueryRepository;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;

/**
 * 为了灵活的支持多条件查询，创建自定义接口
 * @author HeLei
 */
public interface ApplicationHandleHistoryRepositoryCustomization
		extends CriterionQueryRepository<ApplicationHandleHistory> {

}
