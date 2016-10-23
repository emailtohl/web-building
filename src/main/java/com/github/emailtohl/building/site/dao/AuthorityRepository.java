package com.github.emailtohl.building.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.Authority;
/**
 * 授权访问接口
 * @author HeLei
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	Authority findByName(String name);
}
