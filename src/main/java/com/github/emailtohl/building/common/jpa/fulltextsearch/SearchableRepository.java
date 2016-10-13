package com.github.emailtohl.building.common.jpa.fulltextsearch;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriterionQueryRepository;
/**
 * 全文搜索的接口
 * @author HeLei
 *
 * @param <E extends Serializable>
 */
public interface SearchableRepository<E extends Serializable> extends CriterionQueryRepository<E> {
	/**
	 * 全文搜索的分页结果
	 * @param query 查询内容
	 * @param pageable 可分页
	 * @return
	 */
	Page<SearchResult<E>> search(String query, Pageable pageable);
}
