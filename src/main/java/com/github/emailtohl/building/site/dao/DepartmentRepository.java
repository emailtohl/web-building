package com.github.emailtohl.building.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.Department;
/**
 * 部门数据仓库
 * @author HeLei
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
	Department findByName(String name);
}
