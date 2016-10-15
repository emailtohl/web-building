package com.github.emailtohl.building.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.Company;
/**
 * 
 * @author HeLei
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {
	Company findByName(String name);
}
