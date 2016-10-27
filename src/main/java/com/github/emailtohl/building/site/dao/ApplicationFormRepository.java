package com.github.emailtohl.building.site.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
/**
 * 申请表实体仓库
 * @author HeLei
 */
public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {
	ApplicationForm findById(long id);
	Page<ApplicationForm> findByNameLike(String name, Pageable pageable);
	Page<ApplicationForm> findByStatus(Status status, Pageable pageable);
	Page<ApplicationForm> findByApplicantEmailLike(String applicantEmail, Pageable pageable);
}