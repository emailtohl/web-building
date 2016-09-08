package com.github.emailtohl.building.site.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.AccessType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.AbstractCriterionQueryRepository;
import com.github.emailtohl.building.site.dao.UserRepositoryCustomization;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;

/**
 * spring data扫描到接口UserRepository时，会认为UserRepository+Impl作为自定义实现
 * 
 * 当调用UserRepositoryImpl中的方法时，不再代理，而是直接将方法交给UserRepositoryImpl
 * 
 * @author HeLei
 *
 */
@Repository //不由spring管理，而是由spring data管理
public class UserRepositoryImpl extends AbstractCriterionQueryRepository<User> implements UserRepositoryCustomization {

	@Override
	public Pager<User> dynamicQuery(User user, Integer pageNum) {
		return super.getPager(user, pageNum, PAGE_SIZE, AccessType.PROPERTY);
	}
	
	@Override
	public Pager<User> getPageByAuthorities(User user, Pageable pageable) {
		StringBuilder jpql = new StringBuilder("SELECT DISTINCT u FROM User u WHERE 1 = 1");
		String email = user.getEmail();
		Set<Authority> authorities = user.getAuthorities();
		Map<String, Object> args = new HashMap<String, Object>();
		if (authorities != null && authorities.size() > 0) {
			/*
			 * 仅当有一对多关系存在时再插入JOIN语句，否则底层SQL语句就只能查找存在外联关系的数据了
			 */
			int i = jpql.indexOf("WHERE");
			jpql.insert(i, "JOIN u.authorities a ").append(" AND a IN :authorities");
			args.put("authorities", authorities);
		}
		if (email != null && email.length() > 0) {
			jpql.append(" AND u.email LIKE :email");
			args.put("email", email);
		}
		return super.getPager(jpql.toString(), args, pageable.getPageNumber(), pageable.getPageSize());
	}

	/**
	 * 使用spring data使用的page对象，暂不支持Pageable中的排序功能
	 * 默认使用JavaBean属性获取查询条件
	 */
	@Override
	public Page<User> getPage(User user, Pageable pageable) {
		Pager<User> myPager = getPager(user, pageable.getPageNumber(), pageable.getPageSize(), AccessType.PROPERTY);
		Page<User> springPage = new PageImpl<User>(myPager.getContent(), pageable, myPager.getTotalElements());
		return springPage;
	}
	
}
