package com.github.emailtohl.building.common.jpa.envers;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 查询Hibernate envers对实体的审计记录
 * @author HeLei
 * @date 2017.02.04
 *
 * @param <E> 实体类型
 */
public interface AuditedRepository<E extends Serializable> {
	/**
	 * 根据属性名和属性值查询某实体所有历史记录
	 * @param propertyNameValueMap 实体属性名和属性值
	 * @param pageable
	 * @return
	 */
	Page<Tuple<E>> getEntityRevision(Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询某个修订版下所有的历史记录
	 * @param revision
	 * @param propertyNameValueMap
	 * @param pageable
	 * @return
	 */
	Page<E> getEntitiesAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询某个实体在某个修订版时的历史记录
	 * @param id
	 * @param revision
	 * @return
	 */
	E getEntityAtRevision(Long id, Number revision);
	
	/**
	 * 回滚到某历史版本上
	 */
	void rollback(Long id, Number revision);
}
