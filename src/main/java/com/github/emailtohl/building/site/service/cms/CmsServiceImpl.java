package com.github.emailtohl.building.site.service.cms;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final Logger logger = LogManager.getLogger();
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
		return filterUser(articleRepository.findOne(id));
	}
	
	@Override
	public Pager<Article> find(String query, Pageable pageable) {
		Page<Article> page = articleRepository.find(query, pageable);
		List<Article> ls = page.getContent().stream().map(this::filterUser).collect(Collectors.toList());
		return new Pager<>(ls, page.getTotalElements(), page.getNumber(), page.getSize());
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
		if (t != null) {
			a.setType(t);
			t.getArticles().add(a);
		}
		articleRepository.save(a);
		return a.getId();
	}

	@Override
	public void updateArticle(long id, Article article) {
		Article pa = articleRepository.findOne(id);
		if (pa != null) {
			BeanUtils.copyProperties(article, pa, BaseEntity.getIgnoreProperties("author", "type"));
		}
		Type t = article.getType();
		if (t != null) {
			pa.getType().getArticles().remove(pa);
			Type pt = typeRepository.findByName(t.getName());
			pa.setType(pt);
			pt.getArticles().add(pa);
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
		if (StringUtils.hasText(type)) {
			Type t = typeRepository.findByName(type);
			if (t != null) {
				article.setType(t);
			}
		}
		updateArticle(id, article);
	}
	
	@Override
	public void deleteArticle(long id) {
		Article a = articleRepository.findOne(id);
		Type t = a.getType();
		if (t != null) {
			t.getArticles().remove(a);
		}
		articleRepository.delete(a);
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
	public Type findTypeByName(String name) {
		Type t = typeRepository.findByName(name);
		if (t != null)
			t.getArticles().forEach(a -> logger.debug(a));// 激活懒加载的内容
		return t;
	}
	
	@Override
	public long saveType(String name, String description, String parent) {
		Type t = new Type();
		t.setName(name);
		t.setDescription(description);
		if (StringUtils.hasText(parent)) {
			Type p = typeRepository.findByName(parent);
			if (p != null) {
				t.setParent(p);
			}
		}
		typeRepository.save(t);
		return t.getId();
	}

	@Override
	public void updateType(long id, String name, String description, String parent) {
		Type pt = typeRepository.findOne(id);
		if (pt == null)
			return;
		if (StringUtils.hasText(name))
			pt.setName(name);
		if (StringUtils.hasText(description))
			pt.setDescription(description);
		if (StringUtils.hasText(parent)) {
			Type pa = typeRepository.findByName(parent);
			if (pa != null) {
				pt.setParent(pa);
			}
		}
	}

	@Override
	public void deleteType(long id) {
		Type t = typeRepository.findOne(id);
		if (t == null)
			return;
		t.getArticles().forEach(a -> {
			a.setType(null);
		});
		typeRepository.delete(t);
	}

	@Override
	public List<Article> recentArticles() {
		return articleRepository.findAll().stream().limit(10).map(this::filterUser).collect(Collectors.toList());
	}

	@Override
	public List<Comment> recentComments() {
		return commentRepository.findAll().stream().limit(10).collect(Collectors.toList());
	}

	@Override
	public List<Type> getArticleTypes() {
		return typeRepository.findAll();
	}

	@Override
	public Map<Type, List<Article>> classify() {
		return articleRepository.findAll().stream().limit(100).map(this::filterUser)
				.collect(Collectors.groupingBy(article -> {
					Type t = article.getType();
					if (t == null) {
						t = new Type();
						t.setName("未分类");
						t.setDescription("系统不存在的分类");
					}
					return t;
				}));
	}

	@Override
	public WebPage getWebPage(String query) {
		WebPage wp = new WebPage();
		wp.recentArticles = recentArticles();
		wp.recentComments = recentComments();
		wp.categories = classify();
		return wp;
	}
	
	/**
	 * 对于文章来说，只需要展示用户名字，头像等基本信息即可
	 * @param pa
	 * @return
	 */
	private Article filterUser(Article pa) {
		User tu = new User();
		User pu = pa.getAuthor();
		tu.setId(pu.getId());
		tu.setEmail(pu.getEmail());
		tu.setUsername(pu.getUsername());
		tu.setName(pu.getName());
		tu.setIconSrc(pu.getIconSrc());
		
		Article ta = new Article();
		BeanUtils.copyProperties(pa, ta, "author");
		ta.setAuthor(tu);
		return ta;
	}

}
