package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.AUDITED;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import com.github.emailtohl.building.common.jpa.envers.Tuple;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;

/**
 * 查询被审计的实体的历史记录
 * @author HeLei
 */
@PreAuthorize("hasAuthority('" + AUDITED + "')")
public interface AuditedService {
	/**
	 * 根据User的email查询某实体所有历史记录
	 * @param email 
	 * @param pageable
	 * @return
	 */
	Page<Tuple<User>> getUserRevision(String email, Pageable pageable);
	
	/**
	 * 查询User某个修订版下所有的历史记录
	 * @param revision
	 * @param email
	 * @param pageable
	 * @return
	 */
	Page<User> getUsersAtRevision(Number revision, String email, Pageable pageable);
	
	/**
	 * 查询User在某个修订版时的历史记录
	 * @param userId
	 * @param revision
	 * @return
	 */
	User getUserAtRevision(Long userId, Number revision);
	
	/**
	 * 根据Role的名字查询某实体所有历史记录
	 * @param name 实体属性名和属性值
	 * @param pageable
	 * @return
	 */
	Page<Tuple<Role>> getRoleRevision(String name, Pageable pageable);
	
	/**
	 * 查询Role修订版下所有的历史记录
	 * @param revision
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<Role> getRolesAtRevision(Number revision, String name, Pageable pageable);
	
	/**
	 * 查询Role在某个修订版时的历史记录
	 * @param id
	 * @param revision
	 * @return
	 */
	Role getRoleAtRevision(Long roleId, Number revision);
	
	/**
	 * 根据ApplicationForm的名字查询其所有历史记录
	 * @param name ApplicationForm的名字
	 * @param pageable
	 * @return
	 */
	Page<Tuple<ApplicationForm>> getApplicationFormRevision(String name, Pageable pageable);
	
	/**
	 * 查询ApplicationForm修订版下所有的历史记录
	 * @param revision
	 * @param name ApplicationForm的名字
	 * @param pageable
	 * @return
	 */
	Page<ApplicationForm> getApplicationFormsAtRevision(Number revision, String name, Pageable pageable);
	
	/**
	 * 查询ApplicationForm在某个修订版时的历史记录
	 * @param id
	 * @param revision
	 * @return
	 */
	ApplicationForm getApplicationFormAtRevision(Long applicationFormid, Number revision);
}
