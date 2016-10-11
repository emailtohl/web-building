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
import com.github.emailtohl.building.site.dao.ForumPostRepository;
import com.github.emailtohl.building.site.dao.SearchResult;
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
		return new Pager<SearchResult<ForumPostDto>>(ls, page.getTotalElements(), page.getSize());
	}
	
	@Override
	public Pager<ForumPostDto> getPager(Pageable pageable) {
		List<ForumPostDto> ls = new ArrayList<ForumPostDto>();
		int i = 0;
		for (ForumPost entity : forumPostRepository.findAll()) {
			ls.add(convert(entity));
			i++;
		}
		return new Pager<ForumPostDto>(ls, i, pageable.getPageSize());
	} 
	
	@Override
	public ForumPostDto getForumPostByTitle(String title) {
		ForumPost entity = forumPostRepository.findByTitle(title);
		return convert(entity);
	}

	@Override
	@Transactional
	public void save(String email, ForumPostDto dto) {
		ForumPost forumPost = new ForumPost();
		BeanUtils.copyProperties(dto, forumPost, "user");
		User u = userRepository.findByEmail(email);
		if (u == null) {
			throw new IllegalArgumentException("User does not exist.");
		}
		forumPost.setUser(u);
		this.forumPostRepository.save(forumPost);
	}

	@Override
	@Transactional
	public void delete(long id) {
		this.forumPostRepository.delete(id);
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
