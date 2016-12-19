package com.github.emailtohl.building.site.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchResult;
import com.github.emailtohl.building.common.utils.SecurityContextUtils;
import com.github.emailtohl.building.site.dao.ForumPostRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.dto.ForumPostDto;
import com.github.emailtohl.building.site.dto.UserDto;
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
	public Pager<SearchResult<ForumPostDto>> search(String query, Pageable pageable) {
		Page<SearchResult<ForumPost>> page = this.forumPostRepository.search(query, pageable);
		List<SearchResult<ForumPostDto>> ls = new ArrayList<SearchResult<ForumPostDto>>();
		page.getContent().forEach((s1) -> {
			if (s1.getEntity() == null) {
				return;
			}
			ForumPostDto dto = convert(s1.getEntity());
			SearchResult<ForumPostDto> s2 = new SearchResult<ForumPostDto>(dto, s1.getRelevance());
			ls.add(s2);
		});
		return new Pager<SearchResult<ForumPostDto>>(ls, page.getTotalElements(), pageable.getPageNumber(), page.getSize());
	}
	
	@Override
	public Pager<ForumPostDto> getPager(Pageable pageable) {
		Page<ForumPost> page = forumPostRepository.findAll(pageable);
		List<ForumPostDto> ls = new ArrayList<ForumPostDto>();
		page.getContent().forEach(e -> ls.add(convert(e)));
		return new Pager<ForumPostDto>(ls, page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}
	
	@Override
	public List<ForumPostDto> findForumPostByTitle(String title) {
		List<ForumPost> entities = forumPostRepository.findByTitleLike(title.trim() + "%");
		List<ForumPostDto> ls = new ArrayList<>();
		entities.forEach(e -> {
			ls.add(convert(e));
		});
		return ls;
	}

	@Override
	public List<ForumPostDto> findByUserEmail(String userEmail) {
		List<ForumPost> entities = forumPostRepository.findByUserEmail(userEmail);
		List<ForumPostDto> ls = new ArrayList<>();
		entities.forEach(e -> {
			ls.add(convert(e));
		});
		return ls;
	}

	@Override
	public void save(String email, String title, String keywords, String body) {
		ForumPost forumPost = new ForumPost();
		forumPost.setTitle(title);
		forumPost.setKeywords(keywords);
		forumPost.setBody(body);
		User u = userRepository.findByEmail(email);
		if (u == null) {
			throw new IllegalArgumentException("User does not exist.");
		}
		forumPost.setUser(u);
		this.forumPostRepository.save(forumPost);
	}
	
	@Override
	public void save(String title, String keywords, String body) {
		String email = SecurityContextUtils.getCurrentUsername();
		this.save(email, title, keywords, body);
	}
	
	@Override
	public void delete(long id) {
		this.forumPostRepository.delete(id);
	}

	@Override
	public void deleteByEmail(String userEmail) {
		findByUserEmail(userEmail).forEach(dto -> {
			this.forumPostRepository.delete(dto.getId());
		});
		
	}
	
	private ForumPostDto convert(ForumPost entity) {
		ForumPostDto dto = new ForumPostDto();
		BeanUtils.copyProperties(entity, dto, "user");
		UserDto ud = new UserDto();
		BeanUtils.copyProperties(entity.getUser(), ud, "password");
		dto.setUser(ud);
		return dto;
	}

}
