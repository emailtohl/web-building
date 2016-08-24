package com.github.emailtohl.building.site.dao.impl;

import javax.persistence.AccessType;

import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.common.repository.jpa.BaseRepository;
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
@Repository
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepositoryCustomization {

	@Override
	public Pager<User> dynamicQuery(User user, Long pageNum) {
		return super.getPager(user, pageNum, PAGE_SIZE, AccessType.PROPERTY);
	}

}
