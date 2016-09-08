package com.github.emailtohl.building.common.repository.JpaCriterionQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 标准查询接口
 * @param <E> 实体类
 * @author Nick Williams
 */
public interface CriterionQueryRepository<E> {
	/**
	 * 标准查询接口，根据传入的条件List得到一个Page对象
	 * 注意，Pageable的查询是从第0页开始
	 * @param criteria 一个条件List
	 * @param pageable 分页对象
	 * @return
	 */
	Page<E> search(CriteriaList searchCriteria, Pageable pageable);
}
