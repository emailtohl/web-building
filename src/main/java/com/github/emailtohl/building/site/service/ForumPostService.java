package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.FORUM_DELETE;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.Constant;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchResult;
import com.github.emailtohl.building.site.dto.ForumPostDto;
/**
 * 论坛接口
 * @author HeLei
 * @date 2017.02.04
 */
@Validated
@Transactional
public interface ForumPostService {

	/**
	 * 全文搜索接口
	 * @param query
	 * @param pageable
	 * @return
	 */
	Pager<SearchResult<ForumPostDto>> search(String query, Pageable pageable);
	
	/**
	 * 查询所有符合标准的对象
	 * @param query
	 * @return
	 */
	List<ForumPostDto> findAll(String query);
	
	/**
	 * 查询出全部列表然后再做分页
	 * @param query
	 * @param pageable
	 * @return
	 */
	Pager<ForumPostDto> findAllAndPaging(String query, Pageable pageable);
	
	/**
	 * 全文搜索
	 * @param query
	 * @param pageable
	 * @return 只返回查找到的实体类E
	 */
	Pager<ForumPostDto> find(String query, Pageable pageable);
	
	/**
	 * 分页查询所有帖子
	 * @param pageable
	 * @return
	 */
	Pager<ForumPostDto> getPager(Pageable pageable);
	
	/**
	 * 根据标题查找帖子
	 * @param title
	 * @return
	 */
	List<ForumPostDto> findForumPostByTitle(@NotNull String title);
	
	/**
	 * 根据标题查找帖子
	 * @param title
	 * @return
	 */
	List<ForumPostDto> findByUserEmail(@Pattern(regexp = Constant.PATTERN_EMAIL, flags = { Pattern.Flag.CASE_INSENSITIVE }) String userEmail);
	
	/**
	 * 保存帖子，从安全上下文中查找用户名
	 * @param title
	 * @param keywords
	 * @param body
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long save(@NotNull String title, String keywords, String body);
	
	/**
	 * 保存帖子
	 * @param email
	 * @param title
	 * @param keywords
	 * @param body
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long save(@NotNull String email, @NotNull String title, String keywords, String body);
	
	/**
	 * 特殊情况下用于管理员删帖
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + FORUM_DELETE + "')")
	void delete(long id);
	
	/**
	 * 特殊情况下用于管理员删帖
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + FORUM_DELETE + "')")
	void deleteByEmail(String userEmail);
}
