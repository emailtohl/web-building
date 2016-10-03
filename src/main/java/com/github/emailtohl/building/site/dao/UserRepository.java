package com.github.emailtohl.building.site.dao;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.User;
/**
 * 对于增删改查来说，最好使用JpaRepository下的方法，这是由spring data提供，比较方便可靠
 * UserRepositoryCustomization 下的方法主要是使用动态查询
 * 
 * @author HeLei
 */
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustomization {
	@Cacheable
	User findByEmail(String email);
	List<User> findByBirthdayBetween(Date start, Date end);
}
