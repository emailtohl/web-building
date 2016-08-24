package com.github.emailtohl.building.site.dao;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.common.repository.jpa.Repository;
import com.github.emailtohl.building.site.entities.User;

public interface UserRepositoryCustomization extends Repository<User> {
	Integer PAGE_SIZE = 20;
	
	Pager<User> dynamicQuery(User user, Long pageNum);
}
