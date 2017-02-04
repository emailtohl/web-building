package com.github.emailtohl.building.site.dao.audit;

import com.github.emailtohl.building.common.jpa.envers.AuditedRepository;
import com.github.emailtohl.building.site.entities.User;
/**
 * User的历史信息
 * @author HeLei
 * @date 2017.02.04
 */
public interface UserAudit extends AuditedRepository<User> {
}
