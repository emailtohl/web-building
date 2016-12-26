package com.github.emailtohl.building.site.dao.audit;

import com.github.emailtohl.building.common.jpa.envers.AuditedRepository;
import com.github.emailtohl.building.site.entities.User;

public interface UserAudit extends AuditedRepository<User> {

}
