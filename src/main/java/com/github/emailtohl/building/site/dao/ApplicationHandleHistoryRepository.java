package com.github.emailtohl.building.site.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;

/**
 * 查询申请表的历史记录
 * @author HeLei
 */
public interface ApplicationHandleHistoryRepository
		extends JpaRepository<ApplicationHandleHistory, Long>, ApplicationHandleHistoryRepositoryCustomization {

	List<ApplicationHandleHistory> findByApplicationFormId(long id);
	
	Page<ApplicationHandleHistory> findByApplicationFormId(long id, Pageable pageable);

	Page<ApplicationHandleHistory> findByCreateDateBetween(Date start, Date end, Pageable pageable);

	Page<ApplicationHandleHistory> findByCreateDateGreaterThanEqual(Date date, Pageable pageable);

	Page<ApplicationHandleHistory> findByCreateDateLessThanEqual(Date date, Pageable pageable);

	Page<ApplicationHandleHistory> findByHandlerEmailLike(String email, Pageable pageable);

	Page<ApplicationHandleHistory> findByStatus(Status status, Pageable pageable);

	Page<ApplicationHandleHistory> findByStatusAndCreateDateBetween(Status status, Date start, Date end,
			Pageable pageable);

	@Query(value = "select distinct a from ApplicationHandleHistory a where a.applicationForm.applicant.email like ?1 and a.status = ?2 and a.createDate between ?3 and ?4", countQuery = "select distinct count(a) from ApplicationHandleHistory a where a.applicationForm.applicant.email like ?1 and a.status = ?2 and a.createDate between ?3 and ?4")
	Page<ApplicationHandleHistory> history1(String applicantEmail, Status status, Date start, Date end,
			Pageable pageable);

	@Query(value = "select distinct a from ApplicationHandleHistory a where a.handler.email like ?1 and a.status = ?2 and a.createDate between ?3 and ?4", countQuery = "select distinct count(a) from ApplicationHandleHistory a where a.handler.email like ?1 and a.status = ?2 and a.createDate between ?3 and ?4")
	Page<ApplicationHandleHistory> history2(String handlerEmail, Status status, Date start, Date end,
			Pageable pageable);

	@Query(value = "select distinct a from ApplicationHandleHistory a where a.applicationForm.applicant.email like ?1 or a.handler.email like ?2 and a.status = ?3 and a.createDate between ?4 and ?5", countQuery = "select distinct count(a) from ApplicationHandleHistory a where a.applicationForm.applicant.email like ?1 or a.handler.email like ?2 or a.status = ?3 and a.createDate between ?4 and ?5")
	Page<ApplicationHandleHistory> history3(String applicantEmail, String handlerEmail, Status status, Date start,
			Date end, Pageable pageable);
	
	@Query(value = "select distinct a from ApplicationHandleHistory a where a.createDate between ?1 and ?2", countQuery = "select distinct count(a) from ApplicationHandleHistory a where a.createDate between ?1 and ?2")
	Page<ApplicationHandleHistory> history4(Date start, Date end, Pageable pageable);

}
