package com.github.emailtohl.building.site.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 全文搜索仓库
 * @author HeLei
 *
 * @param <T>
 */
public interface SearchableRepository<T> {
	Page<SearchResult<T>> search(String query, Pageable pageable);
}
