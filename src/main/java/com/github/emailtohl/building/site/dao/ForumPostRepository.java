package com.github.emailtohl.building.site.dao;

import org.springframework.data.repository.CrudRepository;

import com.github.emailtohl.building.site.entities.ForumPost;
/**
 * 论坛帖子搜索接口
 * @author HeLei
 */
public interface ForumPostRepository extends CrudRepository<ForumPost, Long>, SearchableRepository<ForumPost> {
	ForumPost findByTitle(String title);
}
