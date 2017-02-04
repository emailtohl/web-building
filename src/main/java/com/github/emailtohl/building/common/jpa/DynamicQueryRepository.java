package com.github.emailtohl.building.common.jpa;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.AccessType;

/**
 * 动态查询接口
 * @author HeLei
 * @date 2017.02.04
 *
 * @param <E>
 */
public interface DynamicQueryRepository<E extends Serializable> {
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
}
