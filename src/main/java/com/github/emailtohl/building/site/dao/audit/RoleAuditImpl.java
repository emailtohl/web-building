package com.github.emailtohl.building.site.dao.audit;

import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.common.jpa.envers.AbstractAuditedRepository;
import com.github.emailtohl.building.site.entities.role.Role;
/**
 * Role的历史信息
 * @author HeLei
 * @date 2017.02.04
 */
@Repository
public class RoleAuditImpl extends AbstractAuditedRepository<Role> implements RoleAudit {

}
