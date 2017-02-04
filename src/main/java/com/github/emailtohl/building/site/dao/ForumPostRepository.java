package com.github.emailtohl.building.site.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchableRepository;
import com.github.emailtohl.building.site.entities.ForumPost;
/**
 * 论坛帖子搜索接口
 * @author HeLei
 * @date 2017.02.04
 */
public interface ForumPostRepository extends JpaRepository<ForumPost, Long>, SearchableRepository<ForumPost> {
	List<ForumPost> findByTitleLike(String title);

	List<ForumPost> findByUserEmail(String email);
}
