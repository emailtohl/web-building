package com.github.emailtohl.building.site.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.common.repository.jpa.JpaRepository;
import com.github.emailtohl.building.site.entities.User;

public interface UserRepositoryCustomization extends JpaRepository<User> {
	Integer PAGE_SIZE = 20;
	
	Pager<User> dynamicQuery(User user, Integer pageNum);
	
	/**
	 * 添加Spring data的分页功能，暂不支持Pageable中的排序功能
	 * 默认使用JavaBean属性获取查询条件
	 */
	Page<User> getPage(User entity, Pageable pageable);
}
