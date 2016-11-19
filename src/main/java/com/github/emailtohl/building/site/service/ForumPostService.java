package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.*;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchResult;
import com.github.emailtohl.building.site.dto.ForumPostDto;
/**
 * 论坛接口
 * @author HeLei
 */
public interface ForumPostService {

	/**
	 * 全文搜索接口
	 * @param query
	 * @param pageable
	 * @return
	 */
	Pager<SearchResult<ForumPostDto>> search(String query, Pageable pageable);
	
	Pager<ForumPostDto> getPager(Pageable pageable);
	
	ForumPostDto getForumPostByTitle(String title);
	
	/**
	 * 为了更好单元测试，用户名不从安全上下文中获取，而通过传参方式
	 * @param email
	 * @param forumPost
	 */
	void save(String email, ForumPostDto forumPost);
	
	/**
	 * 特殊情况下用于管理员删帖
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + FORUM_DELETE + "')")
	void delete(long id);
}
