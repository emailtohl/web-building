package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.baz;
import static com.github.emailtohl.building.initdb.PersistenceData.company;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.product;
import static com.github.emailtohl.building.initdb.PersistenceData.qa;

import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.site.dao.CompanyRepository;
import com.github.emailtohl.building.site.dao.DepartmentRepository;
import com.github.emailtohl.building.site.dao.ForumPostRepository;
import com.github.emailtohl.building.site.dao.UserRepository;

public class CleanTestData {
	
	public static void main(String[] args) {
		
		UserRepository userRepository = Spring.getApplicationContext().getBean(UserRepository.class);
		DepartmentRepository departmentRepository = Spring.getApplicationContext().getBean(DepartmentRepository.class);
		CompanyRepository companyRepository = Spring.getApplicationContext().getBean(CompanyRepository.class);
		ForumPostRepository forumPostRepository = Spring.getApplicationContext().getBean(ForumPostRepository.class);

		
		try {
			forumPostRepository.delete(forumPostRepository.findByUserEmail(emailtohl.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			forumPostRepository.delete(forumPostRepository.findByUserEmail(foo.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			forumPostRepository.delete(forumPostRepository.findByUserEmail(bar.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			forumPostRepository.delete(forumPostRepository.findByUserEmail(baz.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		
		
		try {
//			userRepository.delete(userRepository.findByEmail(emailtohl.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			userRepository.delete(userRepository.findByEmail(foo.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			userRepository.delete(userRepository.findByEmail(bar.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			userRepository.delete(userRepository.findByEmail(baz.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		
		
		try {
			departmentRepository.delete(departmentRepository.findByName(product.getName()));
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			departmentRepository.delete(departmentRepository.findByName(qa.getName()));
		} catch (InvalidDataAccessApiUsageException e) {}
		
		try {
			companyRepository.delete(companyRepository.findByName(company.getName()));
		} catch (InvalidDataAccessApiUsageException e) {}
		
		System.exit(0);
	}
}
