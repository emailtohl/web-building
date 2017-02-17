package com.github.emailtohl.building.site.service.cms;

import static com.github.emailtohl.building.site.entities.role.Authority.FORUM_DELETE;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.cms.Type;

/**
 * cms的服务层接口
 * @author HeLei
 * @data 2017.02.15
 */
@Validated
@Transactional
public interface CmsService {
	
	/**
	 * 获取某文章
	 * @param id
	 * @return
	 */
	Article findArticle(long id);
	
	/**
	 * 全文搜索
	 * @param query
	 * @param pageable
	 * @return 只返回查找到的实体类E
	 */
	Pager<Article> find(String query, Pageable pageable);
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long saveArticle(@NotNull String title, String keywords, String body, Type type);
	
	/**
	 * 保存文章
	 * @param email
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long saveArticle(@NotNull String email, @NotNull String title, String keywords, String body, Type type);
	
	/**
	 * 修改某文章
	 * @param id
	 * @param article
	 */
	@PreAuthorize("isAuthenticated()")
	void updateArticle(long id, Article article);
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + FORUM_DELETE + "')")
	void deleteArticle(long id);
	
	
	/**
	 * 获取某评论
	 * @param id
	 * @return
	 */
	Article findComment(long id);
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param email 用户名为空，则评论为匿名
	 * @param articleId
	 * @param content
	 * @return
	 */
	long saveComment(String email, @Min(1) long articleId, @NotNull String content);
	
	/**
	 * 修改某文章
	 * @param id
	 * @param article
	 */
	@PreAuthorize("isAuthenticated()")
	void updateComment(long id, Article article);
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + FORUM_DELETE + "')")
	void deleteComment(long id);
	
	/**
	 * 最近文章列表
	 * @return
	 */
	List<String> recentArticle();
	
	/**
	 * 最近评论列表
	 * @return
	 */
	List<String> recentComment();
	
	/**
	 * 获取所有的分类
	 * @return
	 */
	List<Type> getArticleTypes();
	
}
