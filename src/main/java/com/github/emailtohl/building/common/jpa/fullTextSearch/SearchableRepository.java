package com.github.emailtohl.building.common.jpa.fullTextSearch;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriterionQueryRepository;
/**
 * 全文搜索的接口
 * @author HeLei
 * @date 2017.02.04
 *
 * @param <E extends Serializable>
 */
public interface SearchableRepository<E extends Serializable> extends CriterionQueryRepository<E> {
	/**
	 * 全文搜索
	 * @param query 查询内容
	 * @param pageable 可分页
	 * @return 一个包含实体类E，相关度以及Lucence的Document的结果集
	 */
	Page<SearchResult<E>> search(String query, Pageable pageable);
	
	/**
	 * 全文搜索
	 * @param query
	 * @param pageable
	 * @return 只返回查找到的实体类E
	 */
	Page<E> find(String query, Pageable pageable);
	
	/**
	 * 查询所有匹配的实体
	 * @param query
	 * @return
	 */
	List<E> findAll(String query);
	
	/**
	 * 查询全部与实体E有关的List后，再做分页
	 * 由于先查询所有列表，然后再在内存中分页，注意性能
	 * @param query
	 * @param pageable
	 * @return
	 */
	Page<E> findAllAndPaging(String query, Pageable pageable);
}
