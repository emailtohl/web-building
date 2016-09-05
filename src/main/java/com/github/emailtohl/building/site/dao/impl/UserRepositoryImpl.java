package com.github.emailtohl.building.site.dao.impl;

import javax.persistence.AccessType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.common.repository.jpa.JpaBaseRepository;
import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.site.dao.UserRepositoryCustomization;
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
public class UserRepositoryImpl extends JpaBaseRepository<User> implements UserRepositoryCustomization {

	@Override
	public Pager<User> dynamicQuery(User user, Integer pageNum) {
		return super.getPager(user, pageNum, PAGE_SIZE, AccessType.PROPERTY);
	}

	/**
	 * 使用spring data使用的page对象，暂不支持Pageable中的排序功能
	 * 默认使用JavaBean属性获取查询条件
	 */
	@Override
	public Page<User> getPage(User user, Pageable pageable) {
		Pager<User> myPager = getPager(user, pageable.getPageNumber(), pageable.getPageSize(), AccessType.PROPERTY);
		Page<User> springPage = new PageImpl<User>(myPager.getDataList(), pageable, myPager.getTotalRow());
		return springPage;
	}
	
}
