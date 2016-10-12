package com.github.emailtohl.building.common.fulltextsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 全文搜索的接口
 * @author HeLei
 *
 * @param <T>
 */
public interface SearchableRepository<T> {
	/**
	 * 全文搜索的分页结果
	 * @param query 查询内容
	 * @param pageable 可分页
	 * @return
	 */
	Page<SearchResult<T>> search(String query, Pageable pageable);
}
