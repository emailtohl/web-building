package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.baz;
import static com.github.emailtohl.building.initdb.PersistenceData.company;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static com.github.emailtohl.building.initdb.PersistenceData.product;
import static com.github.emailtohl.building.initdb.PersistenceData.qa;
import static com.github.emailtohl.building.initdb.PersistenceData.qux;

import org.springframework.context.ApplicationContext;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.github.emailtohl.building.bootspring.Spring;
import com.github.emailtohl.building.site.dao.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.CompanyRepository;
import com.github.emailtohl.building.site.dao.DepartmentRepository;
import com.github.emailtohl.building.site.dao.ForumPostRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;

public class CleanTestData {
	
	public static void main(String[] args) {
		ApplicationContext ctx = Spring.getApplicationContext();
		UserRepository userRepository = ctx.getBean(UserRepository.class);
		DepartmentRepository departmentRepository = ctx.getBean(DepartmentRepository.class);
		CompanyRepository companyRepository = ctx.getBean(CompanyRepository.class);
		ForumPostRepository forumPostRepository = ctx.getBean(ForumPostRepository.class);
		ApplicationFormRepository applicationFormRepository = ctx.getBean(ApplicationFormRepository.class);
		CleanAuditData cleanAuditData = ctx.getBean(CleanAuditData.class);
		
		// 清理论坛发帖
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
			forumPostRepository.delete(forumPostRepository.findByUserEmail(qux.getEmail()));
		} catch (InvalidDataAccessApiUsageException e) {}
		
		// 清理申请单记录
		applicationFormRepository.findByApplicantEmailLike(emailtohl.getEmail())
		.forEach(af -> {
			try {
				applicationFormRepository.delete(af);
			} catch (InvalidDataAccessApiUsageException e) {}
		});
		applicationFormRepository.findByApplicantEmailLike(foo.getEmail())
		.forEach(af -> {
			try {
				applicationFormRepository.delete(af);
			} catch (InvalidDataAccessApiUsageException e) {}
		});
		applicationFormRepository.findByApplicantEmailLike(bar.getEmail())
		.forEach(af -> {
			try {
				applicationFormRepository.delete(af);
			} catch (InvalidDataAccessApiUsageException e) {}
		});
		applicationFormRepository.findByApplicantEmailLike(baz.getEmail())
		.forEach(af -> {
			try {
				applicationFormRepository.delete(af);
			} catch (InvalidDataAccessApiUsageException e) {}
		});
		applicationFormRepository.findByApplicantEmailLike(qux.getEmail())
		.forEach(af -> {
			try {
				applicationFormRepository.delete(af);
			} catch (InvalidDataAccessApiUsageException e) {}
		});
		
		Long id;
		// 清理账户
		try {
			id = userRepository.findByEmail(emailtohl.getEmail()).getId();
			userRepository.delete(id);
			cleanAuditData.cleanUserAudit(id);
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			id = userRepository.findByEmail(foo.getEmail()).getId();
			userRepository.delete(id);
			cleanAuditData.cleanUserAudit(id);
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			id = userRepository.findByEmail(bar.getEmail()).getId();
			userRepository.delete(id);
			cleanAuditData.cleanUserAudit(id);
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			id = userRepository.findByEmail(baz.getEmail()).getId();
			userRepository.delete(id);
			cleanAuditData.cleanUserAudit(id);
		} catch (InvalidDataAccessApiUsageException e) {}
		try {
			id = userRepository.findByEmail(qux.getEmail()).getId();
			userRepository.delete(id);
			cleanAuditData.cleanUserAudit(id);
		} catch (InvalidDataAccessApiUsageException e) {}
		
		// 清理测试的部门和公司信息
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
