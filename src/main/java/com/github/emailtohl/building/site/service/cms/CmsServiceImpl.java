package com.github.emailtohl.building.site.service.cms;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.common.utils.SecurityContextUtil;
import com.github.emailtohl.building.site.dao.cms.ArticleRepository;
import com.github.emailtohl.building.site.dao.cms.CommentRepository;
import com.github.emailtohl.building.site.dao.cms.TypeRepository;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.cms.Comment;
import com.github.emailtohl.building.site.entities.cms.Type;
import com.github.emailtohl.building.site.entities.user.User;

/**
 * cms的服务层实现
 * @author HeLei
 * @data 2017.02.15
 */
@Service
public class CmsServiceImpl implements CmsService {
	@Inject
	TypeRepository typeRepository;
	@Inject
	ArticleRepository articleRepository;
	@Inject
	CommentRepository commentRepository;
	@Inject
	UserRepository userRepository;

	@Override
	public Article findArticle(long id) {
		return articleRepository.findOne(id);
	}
	
	@Override
	public Pager<Article> find(String query, Pageable pageable) {
		Page<Article> page = articleRepository.find(query, pageable);
		return new Pager<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
	}

	@Override
	public long saveArticle(String title, String keywords, String body, String type) {
		return saveArticle(SecurityContextUtil.getCurrentUsername(), title, keywords, body, type);
	}

	@Override
	public long saveArticle(String email, String title, String keywords, String body, String type) {
		Article a = new Article();
		a.setTitle(title);
		a.setKeywords(keywords);
		a.setBody(body);
		User author = userRepository.findByEmail(email);
		a.setAuthor(author);
		Type t = typeRepository.findByName(type);
		a.setType(t);
		articleRepository.save(a);
		return a.getId();
	}

	@Override
	public void updateArticle(long id, Article article) {
		Article p = articleRepository.findOne(id);
		if (p != null) {
			BeanUtils.copyProperties(article, p, BaseEntity.getIgnoreProperties("author"));
		}
	}

	@Override
	public void updateArticle(long id, String title, String keywords, String body, String type) {
		Article article = new Article();
		if (StringUtils.hasText(title))
			article.setTitle(title);
		if (StringUtils.hasText(keywords))
			article.setKeywords(keywords);
		if (StringUtils.hasText(body))
			article.setBody(body);
		if (StringUtils.hasText(title)) {
			Type t =typeRepository.findByName(type);
			article.setType(t);
		}
		updateArticle(id, article);
	}
	
	@Override
	public void deleteArticle(long id) {
		articleRepository.delete(id);
	}

	@Override
	public Comment findComment(long id) {
		return commentRepository.findOne(id);
	}

	@Override
	public long saveComment(String email, long articleId, String content) {
		Article article = articleRepository.findOne(articleId);
		if (article == null) {
			throw new IllegalArgumentException("没有此文章");
		}
		String critics = "匿名", icon = null;
		if (StringUtils.hasText(email)) {
			User u = userRepository.findByEmail(email);
			if (u != null) {
				critics = u.getUsername();
				icon = u.getIconSrc();
			}
		}
		Comment c = new Comment();
		c.setCritics(critics);
		c.setIcon(icon);
		c.setContent(content);
		c.setApproved(false);
		c.setArticle(article);
		commentRepository.save(c);
		return c.getId();
	}
	
	@Override
	public long saveComment(long articleId, String content) {
		String email = SecurityContextUtil.getCurrentUsername();
		return saveComment(email, articleId, content);
	}

	@Override
	public void updateComment(long id, String commentContent) {
		Comment c = commentRepository.findOne(id);
		if (c != null) {
			c.setContent(commentContent);
		}
	}

	@Override
	public void deleteComment(long id) {
		commentRepository.delete(id);
	}

	@Override
	public List<Article> recentArticles() {
		return articleRepository.findAll()
				.stream().limit(10).collect(Collectors.toList());
	}

	@Override
	public List<Comment> recentComments() {
		return commentRepository.findAll()
				.stream().limit(10).collect(Collectors.toList());
	}

	@Override
	public List<Type> getArticleTypes() {
		return typeRepository.findAll();
	}

	@Override
	public Map<Type, List<Article>> classify() {
		return articleRepository.findAll().stream().limit(100)
				.collect(Collectors.groupingBy(article -> article.getType()));
	}

	@Override
	public WebPage getWebPage(String query) {
		WebPage wp = new WebPage();
		wp.recentArticles = recentArticles();
		wp.recentComments = recentComments();
		wp.categories = classify();
		return wp;
	}

}
