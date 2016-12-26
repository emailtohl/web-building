package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.*;

import java.util.Map;

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
	 * @param propertyNameValueMap
	 * @param pageable
	 * @return
	 */
	Page<User> getUsersAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询User在某个修订版时的历史记录
	 * @param userId
	 * @param revision
	 * @return
	 */
	User getUserAtRevision(Long userId, Number revision);
	
	/**
	 * 根据Role属性名和属性值查询某实体所有历史记录
	 * @param propertyNameValueMap 实体属性名和属性值
	 * @param pageable
	 * @return
	 */
	Page<Tuple<Role>> getRoleRevision(Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询Role修订版下所有的历史记录
	 * @param revision
	 * @param propertyNameValueMap
	 * @param pageable
	 * @return
	 */
	Page<Role> getEntitiesAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询Role在某个修订版时的历史记录
	 * @param id
	 * @param revision
	 * @return
	 */
	Role getEntityAtRevision(Long roleId, Number revision);
	
	/**
	 * 根据ApplicationForm属性名和属性值查询某实体所有历史记录
	 * @param propertyNameValueMap 实体属性名和属性值
	 * @param pageable
	 * @return
	 */
	Page<Tuple<ApplicationForm>> getApplicationFormRevision(Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询ApplicationForm修订版下所有的历史记录
	 * @param revision
	 * @param propertyNameValueMap
	 * @param pageable
	 * @return
	 */
	Page<ApplicationForm> getApplicationFormsAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询ApplicationForm在某个修订版时的历史记录
	 * @param id
	 * @param revision
	 * @return
	 */
	ApplicationForm getApplicationFormAtRevision(Long applicationFormid, Number revision);
}
