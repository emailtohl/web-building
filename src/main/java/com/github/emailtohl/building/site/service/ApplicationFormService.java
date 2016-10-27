package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_READ_HISTORY;
import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_TRANSIT;

import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;

/**
 * 流程处理服务
 * @author HeLei
 */
@Service
@Transactional
public interface ApplicationFormService {

	/**
	 * 提交申请
	 * @param applicationForm
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Long application(@Valid ApplicationForm applicationForm);
	
	/**
	 * 查询申请单
	 * @param id
	 * @return
	 */
	ApplicationForm findById(long id);
	
	/**
	 * 模糊查询申请单列表
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<ApplicationForm> findByNameLike(String name, Pageable pageable);
	
	/**
	 * 根据申请单状态查找申请单
	 * @param status
	 * @param pageable
	 * @return
	 */
	Page<ApplicationForm> findByStatus(Status status, Pageable pageable);
	
	/**
	 * 根据申请人查找申请单
	 * @param applicantEmail
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Page<ApplicationForm> findByApplicantEmailLike(Pageable pageable);
	
	/**
	 * 改变申请单状态
	 * 注意：实现需记录处理的历史记录
	 * @param id
	 * @param status
	 * @param cause
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_TRANSIT + "')")
	void transit(Long id, Status status, String cause);
	
	/**
	 * 查询申请单处理历史
	 * @param start
	 * @param end
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByCreateDateBetween(Date start, Date end, Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param date
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByCreateDateGreaterThanEqual(Date date, Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param date
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByCreateDateLessThanEqual(Date date, Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param email
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByHandlerEmailLike(String email, Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param status
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByStatus(Status status, Pageable pageable);
}
