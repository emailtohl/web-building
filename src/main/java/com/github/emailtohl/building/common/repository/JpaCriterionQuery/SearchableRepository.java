package com.github.emailtohl.building.common.repository.JpaCriterionQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 标准查询接口
 * @param <E> 实体类
 */
public interface SearchableRepository<E> {
	/**
	 * 注意，Pageable的查询是从第0页开始
	 */
	Page<E> search(SearchCriteria searchCriteria, Pageable pageable);
}
