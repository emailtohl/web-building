package com.github.emailtohl.building.common.repository.jpa;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.AccessType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.repository.JpaCriterionQuery.SearchCriteria;
import com.github.emailtohl.building.common.repository.generic.GenericRepository;
/**
 * 业务代码的数据仓库接口，可除了继承的增删改查以外，新增获取Pager对象的接口
 * @author HeLei
 * @param <E>
 */
public interface JpaRepository<E extends Serializable> extends GenericRepository<Long, E> {
	/**
	 * 动态查询一个Pager对象
	 * @param jpql 传入的JPQL查询语句
	 * @param args JPQL对应的参数数组
	 * @param pageNum 查询第几页
	 * @param pageSize 每页有多少行
	 * @return 一个Pager对象，包含查询结果的列表、当前页、最大页、最大行等信息
	 */
	Pager<E> getPager(String jpql, Object[] args, Integer pageNum, Integer pageSize);

	/**
	 * 动态查询一个Pager对象
	 * @param jpql 传入的JPQL查询语句
	 * @param args 参数以Map形式传入
	 * @param pageNum 查询第几页
	 * @param pageSize 每页有多少行
	 * @return 一个Pager对象，包含查询结果的列表、当前页、最大页、最大行等信息
	 */
	Pager<E> getPager(String jpql, Map<String, Object> args, Integer pageNum, Integer pageSize);

	/**
	 * 动态查询一个Pager对象
	 * @param entity
	 * @param pageNum 查询第几页
	 * @param pageSize 每页有多少行
	 * @param type 获取实体对象的方式，FIELD是直接读取实体的字段，PROPERTY是读取实体的JavaBean属性
	 * @return 一个Pager对象，包含查询结果的列表、当前页、最大页、最大行等信息
	 */
	Pager<E> getPager(E entity, Integer pageNum, Integer pageSize, AccessType type);

	/**
	 * 标准查询接口，根据传入的条件List得到一个Page对象
	 * @param criteria 一个条件List
	 * @param pageable 分页对象
	 * @return
	 */
	Page<E> search(SearchCriteria searchCriteria, Pageable pageable);
	
	/**
	 * 同步数据
	 */
	void flush();

	/**
	 * 刷新实体对象
	 * @param entity 实体对象
	 */
	void refresh(E entity);

	/**
	 * 设置为游离状态
	 * @param entity 实体对象
	 */
	void detach(E entity);
	
	/**
	 * 判断实体是否处于持久化状态
	 * @param entity 实体对象
	 * @return 是否为托管状态
	 */
	boolean isManaged(E entity);

	/**
	 * 关闭entityManager
	 */
	void close();
}
