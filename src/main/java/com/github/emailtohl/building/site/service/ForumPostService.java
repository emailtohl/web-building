package com.github.emailtohl.building.site.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.site.dao.SearchResult;
import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;
/**
 * 论坛接口
 * @author HeLei
 */
public interface ForumPostService {
	User getUser(String username);

	Page<SearchResult<ForumPost>> search(String query, Pageable pageable);

	void save(ForumPost forumPost);
	
	void delete(ForumPost forumPost);
}
