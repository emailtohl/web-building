package com.github.emailtohl.building.site.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.User;
/**
 * 对于增删改查来说，最好使用JpaRepository下的方法，这是由spring data提供，比较方便可靠
 * UserRepositoryCustomization 下的方法主要是使用动态查询
 * 
 * @author HeLei
 */
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustomization {
	User findByEmail(String email);
	List<User> findByBirthdayBetween(Date start, Date end);@Override
	default <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
}
