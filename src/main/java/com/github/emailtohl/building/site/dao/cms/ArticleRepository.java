package com.github.emailtohl.building.site.dao.cms;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.cms.Article;

/**
 * 文章实体的数据访问接口
 * @author HeLei
 * @date 2017.02.12
 */
public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustomization {
	
}
