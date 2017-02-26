package com.github.emailtohl.building.site.service.cms;

import static com.github.emailtohl.building.site.entities.role.Authority.CONTENT_MANAGER;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.cms.Comment;
import com.github.emailtohl.building.site.entities.cms.Type;

/**
 * cms的服务层接口
 * @author HeLei
 * @data 2017.02.15
 */
@Validated
@Transactional
public interface CmsService {
	String CACHE_NAME_ARTICLE = "articleCache";
	String CACHE_NAME_TYPE = "typeCache";
	/**
	 * 获取某文章
	 * @param id
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Article getArticle(long id);
	
	/**
	 * 全文搜索
	 * @param query
	 * @param pageable
	 * @return 只返回查找到的实体类E
	 */
	@PreAuthorize("isAuthenticated()")
	Pager<Article> searchArticles(String query, Pageable pageable);
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE, allEntries = true) // 当这个方法被调用后，即会清空缓存
	@PreAuthorize("isAuthenticated()")
	long saveArticle(@NotNull String title, String keywords, String body, String type);
	
	/**
	 * 保存文章
	 * @param email
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE, allEntries = true)
	@PreAuthorize("isAuthenticated()")
	long saveArticle(@NotNull String email, @NotNull String title, String keywords, String body, String type);
	
	/**
	 * 修改某文章
	 * @param id
	 * @param article
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE, allEntries = true)
	@PreAuthorize("isAuthenticated()")
	void updateArticle(long id, Article article);
	
	/**
	 * 修改某文章
	 * @param id
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE)
	@PreAuthorize("isAuthenticated()")
	void updateArticle(long id, String title, String keywords, String body, String type);
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void deleteArticle(long id);

	/**
	 * 让文章发表
	 * @param articleId
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void approveArticle(long articleId);
	
	/**
	 * 拒绝文章发布
	 * @param articleId
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void rejectArticle(long articleId);
	
	/**
	 * 开放评论
	 * @param articleId
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void openComment(long articleId);
	
	/**
	 * 关闭评论
	 * @param articleId
	 */
	@CacheEvict(value = CACHE_NAME_ARTICLE)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void closeComment(long articleId);
	
	/**
	 * 获取某评论
	 * @param id
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Comment findComment(long id);
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param email 用户名
	 * @param articleId
	 * @param content
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && #email == principal.username)")
	long saveComment(@NotNull String email, @Min(1) long articleId, @NotNull String content);

	/**
	 * 保存评论，从安全上下文中查找用户名
	 * @param articleId
	 * @param content
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long saveComment(@Min(1) long articleId, @NotNull String content);
	
	/**
	 * 修改评论
	 * @param email 用户名
	 * @param id 评论的id
	 * @param commentContent 评论的内容
	 */
	@PreAuthorize("isAuthenticated() && #email == principal.username)")
	void updateComment(@NotNull String email, @Min(1) long id, @NotNull String commentContent);
	
	/**
	 * 修改评论
	 * @param id 评论的id
	 * @param commentContent 评论的内容
	 */
	@PreAuthorize("isAuthenticated()")
	void updateComment(@Min(1) long id, @NotNull String commentContent);
	
	/**
	 * 删除评论
	 * @param id 评论id
	 */
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void deleteComment(@Min(1) long id);
	
	/**
	 * 允许评论发表
	 * @param commentId
	 */
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void approvedComment(long commentId);
	
	/**
	 * 拒绝评论发表
	 * @param commentId
	 */
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void rejectComment(long commentId);
	
	/**
	 * 获取所有的文章分类
	 * @return
	 */
	@Cacheable(value = CACHE_NAME_TYPE)
	@PreAuthorize("isAuthenticated()")
	List<Type> getTypes();
	
	/**
	 * 分页查询文章类型
	 * @param typeName
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Pager<Type> getTypePager(String typeName, Pageable pageable);
	
	/**
	 * 根据id查找文章类型
	 * @param id
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Type findTypeById(long id);
	
	/**
	 * 通过名字查询文章类型
	 * @param name
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Type findTypeByName(@NotNull String name);

	/**
	 * 保存一个文章类型
	 * @param name
	 * @param description
	 * @param parent
	 * @return
	 */
	@CacheEvict(value = CACHE_NAME_TYPE, allEntries = true)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	long saveType(@NotNull String name, String description, String parent);
	
	/**
	 * 更新一个文章类型
	 * @param name
	 * @param description
	 * @param parent 类型的父类型，如果为null则为顶级类型
	 */
	@CacheEvict(value = CACHE_NAME_TYPE, allEntries = true)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void updateType(@Min(1) long id, @NotNull String name, String description, String parent);
	
	/**
	 * 删除一个文章类型
	 * @param id
	 */
	@CacheEvict(value = CACHE_NAME_TYPE, allEntries = true)
	@PreAuthorize("hasAuthority('" + CONTENT_MANAGER + "')")
	void deleteType(@Min(1) long id);
	
	/**
	 * 最近文章列表
	 * @return
	 */
	// 从CACHE_NAME的缓存中查询，有则返回缓存中的对象，无则执行实际的方法，并将执行的结果存入缓存中
	@Cacheable(value = CACHE_NAME_ARTICLE)
	List<Article> recentArticles();
	
	/**
	 * 打开文章
	 * @param id
	 * @return
	 */
	Article readArticle(long id);
	
	/**
	 * 最近评论列表
	 * @return
	 */
	List<Comment> recentComments();
	
	/**
	 * 根据文章类型进行分类
	 * @return
	 */
	Map<Type, List<Article>> classify();
	
}
