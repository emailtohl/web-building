package com.github.emailtohl.building.site.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.Company;
/**
 * 公司的缓存
 * @author HeLei
 * @date 2017.02.04
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {
	String CACHE_NAME = "companyCache";
	
	@Cacheable(CACHE_NAME)
	Company findByName(String name);
}
