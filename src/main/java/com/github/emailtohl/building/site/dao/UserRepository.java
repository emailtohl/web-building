package com.github.emailtohl.building.site.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustomization {
	User findByEmail(String email);
	List<User> findByBirthdayBetween(Date start, Date end);
}
