package com.github.emailtohl.building.site.dao;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;

/**
 * 查询申请表的历史记录
 * @author HeLei
 */
public interface ApplicationHandleHistoryRepository extends JpaRepository<ApplicationHandleHistory, Long> {
	Page<ApplicationHandleHistory> findByCreateDateBetween(Date start, Date end, Pageable pageable);
	Page<ApplicationHandleHistory> findByCreateDateGreaterThanEqual(Date date, Pageable pageable);
	Page<ApplicationHandleHistory> findByCreateDateLessThanEqual(Date date, Pageable pageable);
	Page<ApplicationHandleHistory> findByHandlerEmailLike(String email, Pageable pageable);
	Page<ApplicationHandleHistory> findByStatus(Status status, Pageable pageable);
}
