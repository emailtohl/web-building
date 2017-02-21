package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.exception.VerifyFailure;
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
	@RequestMapping(value = "cms/article/{id}", method = GET)
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
	@RequestMapping(value = "cms/article/search", method = GET)
	@ResponseBody
	public Pager<Article> search(@RequestParam(name="query", required = false, defaultValue = "") String query, 
			@PageableDefault(page = 0, size = 10, sort = {"title", "keywords"}, direction = Direction.DESC) Pageable pageable) {
		return cmsService.find(query, pageable);
	}
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param form 前端提交的表单数据
	 * @param e 若校验失败后，存储的失败信息
	 */
	@RequestMapping(value = "cms/article", method = POST)
	@ResponseBody
	public void saveArticle(@RequestBody @Valid ArticleForm form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			throw new VerifyFailure(e.toString());
		}
		cmsService.saveArticle(form.title, form.keywords, form.body, form.type);
	}
	
	/**
	 * 文章的表单信息
	 */
	class ArticleForm implements Serializable {
		private static final long serialVersionUID = 2897739809930401319L;
		@NotNull String title;
		String keywords;
		@NotNull String body;
		String type;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getKeywords() {
			return keywords;
		}
		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		@Override
		public String toString() {
			return "ArticleForm [title=" + title + ", keywords=" + keywords + ", body=" + body + ", type=" + type + "]";
		}
	}
	
	/**
	 * 修改某文章
	 * @param id 文章id
	 * @param article 修改的信息
	 * @param article 存储校验失败的信息
	 */
	@RequestMapping(value = "cms/article/{id}", method = PUT)
	@ResponseBody
	public void updateArticle(@PathVariable long id, @RequestBody @Valid ArticleForm form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			throw new VerifyFailure(e.toString());
		}
		cmsService.updateArticle(id, form.title, form.keywords, form.body, form.type);
	}
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@RequestMapping(value = "cms/article/{id}", method = DELETE)
	@ResponseBody
	void deleteArticle(@PathVariable long id) {
		cmsService.deleteArticle(id);
	}
	
	/**
	 * 获取某评论
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "cms/comment/{id}", method = GET)
	@ResponseBody
	public Comment findComment(long id) {
		return cmsService.findComment(id);
	}
	
	/**
	 * 保存评论，从安全上下文中查找用户名
	 * @param articleId 被评论文章的ID
	 * @param content 评论的内容
	 */
	@RequestMapping(value = "cms/comment", method = POST)
	@ResponseBody
	public void saveComment(@RequestParam(required = true, name = "articleId") long articleId,
			@RequestParam(required = true, name = "content") String content) {
		cmsService.saveComment(articleId, content);
	}
	
	/**
	 * 修改评论
	 * @param id 评论的id
	 * @param commentContent 评论的内容
	 */
	@RequestMapping(value = "cms/comment/{id}", method = PUT)
	@ResponseBody
	public void updateComment(@PathVariable("id") long id, 
			@RequestParam(required = true, name = "commentContent") String commentContent) {
		cmsService.updateComment(id, commentContent);
	}
	
	/**
	 * 删除评论
	 * @param id 评论id
	 */
	@RequestMapping(value = "cms/comment/{id}", method = DELETE)
	@ResponseBody
	void deleteComment(@PathVariable long id) {
		cmsService.deleteComment(id);
	}
	
	/**
	 * 通过名字查询文章类型
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "cms/type", method = GET)
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
	@RequestMapping(value = "cms/type", method = POST)
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
	@RequestMapping(value = "cms/type/{id}", method = PUT)
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
	@RequestMapping(value = "cms/type", method = DELETE)
	@ResponseBody
	public void deleteType(@Min(1) long id) {
		cmsService.deleteType(id);
	}
	
	/**
	 * 最近文章列表
	 * @return
	 */
	@RequestMapping(value = "public/recentArticles", method = GET)
	public List<Article> recentArticles() {
		return cmsService.recentArticles();
	}
	
	/**
	 * 最近评论列表
	 * @return
	 */
	@RequestMapping(value = "public/recentComments", method = GET)
	public List<Comment> recentComments() {
		return cmsService.recentComments();
	}
	
	/**
	 * 获取所有的分类
	 * @return
	 */
	@RequestMapping(value = "public/articleTypes", method = GET)
	public List<Type> getArticleTypes() {
		return cmsService.getArticleTypes();
	}
	
	/**
	 * 根据文章类型进行分类
	 * @return
	 */
	@RequestMapping(value = "public/classify", method = GET)
	public Map<Type, List<Article>> classify() {
		return cmsService.classify();
	}
	
	/**
	 * 获取web页面所需要的数据
	 * @param query 搜索页面的参数，可以为null
	 * @return
	 */
	@RequestMapping(value = "public/webPage", method = GET)
	public WebPage getWebPage(String query) {
		return cmsService.getWebPage(query);
	}
}
