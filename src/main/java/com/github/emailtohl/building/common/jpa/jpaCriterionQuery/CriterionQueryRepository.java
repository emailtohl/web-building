package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.DynamicQueryRepository;
/**
 * 标准查询接口
 * 让该接口继承DynamicQueryRepository，即获得动态查询的能力，也获得本接口提供的功能
 * @param <E> 实体类
 * @author HeLei
 */
public interface CriterionQueryRepository<E extends Serializable> extends DynamicQueryRepository<E> {
	/**
	 * 标准查询接口，根据传入的条件集合得到一个Page对象 注意，Pageable的查询是从第0页开始，条件集合之间是AND关系
	 * 
	 * @param criteria 一个条件集合
	 * @param pageable 分页对象
	 * @return
	 */
	Page<E> search(Collection<Criterion> criteriaList, Pageable pageable);
}
