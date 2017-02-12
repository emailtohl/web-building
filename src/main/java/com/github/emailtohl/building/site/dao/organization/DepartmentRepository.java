package com.github.emailtohl.building.site.dao.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.organization.Department;
/**
 * 部门数据仓库
 * @author HeLei
 * @date 2017.02.04
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
	Department findByName(String name);
}
