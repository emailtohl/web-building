package com.github.emailtohl.building.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchableRepository;
import com.github.emailtohl.building.site.entities.ForumPost;
/**
 * 论坛帖子搜索接口
 * @author HeLei
 */
public interface ForumPostRepository extends JpaRepository<ForumPost, Long>, SearchableRepository<ForumPost> {
	ForumPost findByTitle(String title);
	ForumPost findByUserEmail(String email);
}
