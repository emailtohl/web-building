package com.github.emailtohl.building.site.service.impl;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.emailtohl.building.site.dao.ForumPostRepository;
import com.github.emailtohl.building.site.dao.SearchResult;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.ForumPost;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ForumPostService;

/**
 * 论坛Service实现
 * 
 * @author HeLei
 */
@Service
public class ForumPostServiceImpl implements ForumPostService {
	@Inject
	UserRepository userRepository;
	@Inject
	ForumPostRepository forumPostRepository;

	@Override
	@Transactional
	public User getUser(String email) {
		return this.userRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public Page<SearchResult<ForumPost>> search(String query, Pageable pageable) {
		return this.forumPostRepository.search(query, pageable);
	}

	@Override
	@Transactional
	public void save(ForumPost forumPost) {
		this.forumPostRepository.save(forumPost);
	}

	@Override
	@Transactional
	public void delete(ForumPost forumPost) {
		this.forumPostRepository.delete(forumPost);
	}
}
