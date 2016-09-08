package com.github.emailtohl.building.site.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriterionQueryRepository;
import com.github.emailtohl.building.site.entities.User;

public interface UserRepositoryCustomization extends CriterionQueryRepository<User> {
	Integer PAGE_SIZE = 20;
	
	Pager<User> dynamicQuery(User user, Integer pageNum);
	
	/**
	 * 根据用户授权来查询Page，由于User与Authority是多对多关系，自定义的动态查询不满足需求，所以新开辟一个接口
	 * @param user
	 * @param pageable
	 * @return
	 */
	Pager<User> getPageByAuthorities(User user, Pageable pageable);
	
	/**
	 * 添加Spring data的分页功能，暂不支持Pageable中的排序功能
	 * 默认使用JavaBean属性获取查询条件
	 */
	Page<User> getPage(User entity, Pageable pageable);
}
