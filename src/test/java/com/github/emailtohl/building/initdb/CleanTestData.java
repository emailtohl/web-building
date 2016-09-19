package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.*;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.site.dao.CompanyRepository;
import com.github.emailtohl.building.site.dao.DepartmentRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.Company;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.User;

public class CleanTestData {
	
	public static void main(String[] args) {
		
		UserRepository userRepository = SpringUtils.context.getBean(UserRepository.class);
		DepartmentRepository departmentRepository = SpringUtils.context.getBean(DepartmentRepository.class);
		CompanyRepository companyRepository = SpringUtils.context.getBean(CompanyRepository.class);
		for (User u : userRepository.findAll()) {
			if (emailtohl.getEmail().equals(u.getEmail()) || foo.getEmail().equals(u.getEmail()) || bar.getEmail().equals(u.getEmail())) {
				userRepository.delete(u);
			}
		}
		for (Department d : departmentRepository.findAll()) {
			if (product.getName().equals(d.getName()) || qa.getName().equals(d.getName())) {
				departmentRepository.delete(d);
			}
		}
		for (Company c : companyRepository.findAll()) {
			if (company.getName().equals(c.getName())) {
				companyRepository.delete(c);
			}
		}
		
		System.exit(0);
	}
}
