package com.github.emailtohl.building.site.controller;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.cms.Comment;
import com.github.emailtohl.building.site.entities.cms.Type;
import com.github.emailtohl.building.site.service.cms.CmsService;
import com.github.emailtohl.building.site.service.cms.WebPage;

import freemarker.template.Configuration;
/**
 * 内容管理的控制器
 * @author HeLei
 * @date 2017.02.16
 */
@Controller
public class CmsCtrl {
	private static final Logger logger = LogManager.getLogger();
	@Inject File resourcePath;
	@Inject Configuration cfg;
	@Inject CmsService cmsService;

	/**
	 * 获取某文章
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "cms/article/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Article findArticle(@PathVariable long id) {
		return cmsService.findArticle(id);
	}
	
	/**
	 * 全文搜索
	 * @param query
	 * @param pageable
	 * @return 只返回查找到的实体类E
	 */
	@RequestMapping(value = "cms/article/query", method = RequestMethod.GET)
	@ResponseBody
	public Pager<Article> find(@RequestParam(name="param", required = false, defaultValue = "") String query, 
			@PageableDefault(page = 0, size = 10, sort = {"title", "keywords"}, direction = Direction.DESC) Pageable pageable) {
		return cmsService.find(query, pageable);
	}
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "cms/article", method = RequestMethod.POST)
	@ResponseBody
	public void saveArticle(@RequestBody @Valid ArticleForm form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return;
		}
		cmsService.saveArticle(form.title, form.keywords, form.body, form.type);
	}
	
	class ArticleForm implements Serializable {
		private static final long serialVersionUID = 2897739809930401319L;
		@NotNull String title;
		String keywords;
		@NotNull String body;
		String type;
	}
	
	/**
	 * 修改某文章
	 * @param id
	 * @param article
	 */
	@RequestMapping(value = "cms/article/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateArticle(@PathVariable long id, @RequestBody @Valid ArticleForm form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return;
		}
		cmsService.updateArticle(id, form.title, form.keywords, form.body, form.type);
	}
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@RequestMapping(value = "cms/article/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	void deleteArticle(@PathVariable long id) {
		cmsService.deleteArticle(id);
	}
	
	/**
	 * 获取某评论
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "cms/comment/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Comment findComment(long id) {
		return cmsService.findComment(id);
	}
	
	/**
	 * 保存文章，从安全上下文中查找用户名，若在上下文找不到用户，则评论为匿名
	 * @param articleId
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "cms/comment", method = RequestMethod.POST)
	@ResponseBody
	public void saveComment(@RequestParam(required = true, name = "articleId") long articleId,
			@RequestParam(required = true, name = "content") String content) {
		cmsService.saveComment(articleId, content);
	}
	
	/**
	 * 修改某文章
	 * @param id
	 * @param article
	 */
	@RequestMapping(value = "cms/comment/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateComment(@PathVariable("id") long id, 
			@RequestParam(required = true, name = "commentContent") String commentContent) {
		cmsService.updateComment(id, commentContent);
	}
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@RequestMapping(value = "cms/comment/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	void deleteComment(@PathVariable long id) {
		cmsService.deleteComment(id);
	}
	
	/**
	 * 通过名字查询文章类型
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "cms/type", method = RequestMethod.GET)
	@ResponseBody
	public Type findTypeByName(@RequestParam("name") String name) {
		return cmsService.findTypeByName(name);
	}

	/**
	 * 保存一个文章类型
	 * @param name
	 * @param description
	 * @param parent
	 * @return
	 */
	@RequestMapping(value = "cms/type", method = RequestMethod.POST)
	@ResponseBody
	public long saveType(@RequestParam(name = "name", required = true) String name, 
			@RequestParam(name = "description", required = false) String description, 
			@RequestParam(name = "parent", required = false) String parent) {
		return saveType(name, description, parent);
	}
	
	/**
	 * 更新一个文章类型
	 * @param name
	 * @param description
	 * @param parent 类型的父类型，如果为null则为顶级类型
	 */
	@RequestMapping(value = "cms/type/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateType(@PathVariable("id") long id, 
			@RequestParam(name = "name", required = false) String name, 
			@RequestParam(name = "description", required = false) String description, 
			@RequestParam(name = "parent", required = false) String parent) {
		cmsService.updateType(id, name, description, parent);
	}
	
	/**
	 * 删除一个文章类型
	 * @param id
	 */
	@RequestMapping(value = "cms/type", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteType(@Min(1) long id) {
		cmsService.deleteType(id);
	}
	
	/**
	 * 最近文章列表
	 * @return
	 */
	@RequestMapping(value = "public/recentArticles", method = RequestMethod.GET)
	public List<Article> recentArticles() {
		return cmsService.recentArticles();
	}
	
	/**
	 * 最近评论列表
	 * @return
	 */
	@RequestMapping(value = "public/recentComments", method = RequestMethod.GET)
	public List<Comment> recentComments() {
		return cmsService.recentComments();
	}
	
	/**
	 * 获取所有的分类
	 * @return
	 */
	@RequestMapping(value = "public/articleTypes", method = RequestMethod.GET)
	public List<Type> getArticleTypes() {
		return cmsService.getArticleTypes();
	}
	
	/**
	 * 根据文章类型进行分类
	 * @return
	 */
	@RequestMapping(value = "public/classify", method = RequestMethod.GET)
	public Map<Type, List<Article>> classify() {
		return cmsService.classify();
	}
	
	/**
	 * 获取web页面所需要的数据
	 * @param query 搜索页面的参数，可以为null
	 * @return
	 */
	@RequestMapping(value = "public/webPage", method = RequestMethod.GET)
	public WebPage getWebPage(String query) {
		return cmsService.getWebPage(query);
	}
}
