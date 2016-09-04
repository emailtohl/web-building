package com.github.emailtohl.building.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.Manager;
/**
 * UserRepository仅有User信息，所以建立一个EmployRepository能查询Employ、Manager的信息
 * @author HeLei
 *
 */
public interface EmployRepository extends JpaRepository<Manager, Long> {
	Manager findByEmail(String email);
}
