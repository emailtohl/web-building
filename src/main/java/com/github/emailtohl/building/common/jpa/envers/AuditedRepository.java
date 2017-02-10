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
	 * 查询某实体所有的历史记录，并根据谓词进行筛选
	 * @param propertyNameValueMap 查询的谓词，使用AND关系过滤查询结果，可为空
	 * @param pageable
	 * @return 分页的元组列表，元组中包含版本详情，实体在该版本时的状态以及该版本的操作（增、改、删）
	 */
	Page<Tuple<E>> getEntityRevision(Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询某个修订版下，该实体类的所有的历史记录，但不包括删除时的
	 * 例如创建一批用户，这是“增加”类型的修订版，该版本就关联着这一批用户实体
	 * @param revision 版本号，通过AuditReader#getRevisions(Entity.class, ID)获得
	 * @param propertyNameValueMap 查询的谓词，使用AND关系过滤查询结果，可为空
	 * @param pageable
	 * @return
	 */
	Page<E> getEntitiesAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable);
	
	/**
	 * 查询某个实体在某个修订版时的历史记录
	 * @param id 实体的id
	 * @param revision 版本号，通过AuditReader#getRevisions(Entity.class, ID)获得
	 * @return
	 */
	E getEntityAtRevision(Long id, Number revision);
	
	/**
	 * 回滚到某历史版本上
	 */
	void rollback(Long id, Number revision);
}
