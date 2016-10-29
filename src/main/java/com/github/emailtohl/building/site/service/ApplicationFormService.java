package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_READ_HISTORY;
import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_TRANSIT;

import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;

/**
 * 流程处理服务
 * @author HeLei
 */
@Transactional
@Validated
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
	Page<ApplicationForm> findByNameLike(String name, @NotNull Pageable pageable);
	
	/**
	 * 根据申请单状态查找申请单
	 * @param status
	 * @param pageable
	 * @return
	 */
	Page<ApplicationForm> findByStatus(@NotNull Status status, @NotNull Pageable pageable);
	
	/**
	 * 根据申请单状态查找申请单
	 * @param status
	 * @param pageable
	 * @return
	 */
	Page<ApplicationForm> findByNameAndStatus(String name, Status status, @NotNull Pageable pageable);
	
	/**
	 * 根据申请人查找申请单
	 * @param applicantEmail
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Page<ApplicationForm> findMyApplicationForm(@NotNull Pageable pageable);
	
	/**
	 * 改变申请单状态
	 * 注意：实现需记录处理的历史记录
	 * @param id
	 * @param status
	 * @param cause
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_TRANSIT + "')")
	void transit(@NotNull Long id, @NotNull Status status, String cause);
	
	/**
	 * 查询申请单处理历史
	 * @param start
	 * @param end
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByCreateDateBetween(@NotNull Date start, @NotNull Date end, @NotNull Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param date
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByCreateDateGreaterThanEqual(@NotNull Date date, @NotNull Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param date
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByCreateDateLessThanEqual(@NotNull Date date, @NotNull Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param email
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByHandlerEmailLike(@NotNull String email, @NotNull Pageable pageable);
	
	/**
	 * 查询申请单处理历史
	 * @param status
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> historyFindByStatus(@NotNull Status status, @NotNull Pageable pageable);
	
	/**
	 * 
	 * @param status
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	Page<ApplicationHandleHistory> history(String applicantEmail, String handlerEmail, @NotNull Status status, @NotNull Date start, @NotNull Date end, @NotNull Pageable pageable);
}
