package com.github.emailtohl.building.site.service.cms;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.common.utils.SecurityContextUtil;
import com.github.emailtohl.building.site.dao.cms.ArticleRepository;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.user.User;

/**
 * cms的服务层实现
 * @author HeLei
 * @data 2017.02.15
 */
@Service
public class CmsServiceImpl implements CmsService {
	@Inject
	ArticleRepository articleRepository;
	@Inject
	UserRepository userRepository;

	@Override
	public Article findOne(long id) {
		return articleRepository.findOne(id);
	}
	
	@Override
	public List<Article> findAll(String query) {
		return articleRepository.findAll();
	}

	@Override
	public Pager<Article> find(String query, Pageable pageable) {
		Page<Article> page = articleRepository.find(query, pageable);
		return new Pager<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
	}

	@Override
	public long save(String title, String keywords, String body, String type) {
		return save(SecurityContextUtil.getCurrentUsername(), title, keywords, body, type);
	}

	@Override
	public long save(String email, String title, String keywords, String body, String type) {
		Article a = new Article();
		a.setTitle(title);
		a.setKeywords(keywords);
		a.setBody(body);
		User author = userRepository.findByEmail(email);
		a.setAuthor(author);
		articleRepository.save(a);
		a.setType(type);
		return a.getId();
	}

	@Override
	public void update(long id, Article article) {
		Article p = articleRepository.findOne(id);
		if (p != null) {
			BeanUtils.copyProperties(article, p, BaseEntity.getIgnoreProperties("author"));
		}
	}
	
	@Override
	public void delete(long id) {
		articleRepository.delete(id);
	}

}
