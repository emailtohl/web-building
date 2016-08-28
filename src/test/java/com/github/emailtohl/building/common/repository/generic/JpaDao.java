package com.github.emailtohl.building.common.repository.generic;

import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.site.entities.User;

@Repository
public class JpaDao extends GenericJpaRepository<Long, User> {
	public JpaDao() {
		System.out.println("JpaDaoTest init");
	}
}
