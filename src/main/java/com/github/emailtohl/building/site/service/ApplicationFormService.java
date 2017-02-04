package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_DELETE;
import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_READ_HISTORY;
import static com.github.emailtohl.building.site.entities.Authority.APPLICATION_FORM_TRANSIT;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
/**
 * 流程处理服务
 * @author HeLei
 * @date 2017.02.04
 */
@Transactional
@Validated
public interface ApplicationFormService {

	/**
	 * 提交申请，从安全上下文获取被认证的用户作为申请人实体
	 * @param applicationForm
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Long application(String name, String description);
	
	/**
	 * 查询申请单
	 * 需要被认证的用户才能调用，如果不是申请者本人，则需具备APPLICATION_FORM_TRANSIT权限
	 * @param id
	 * @return
	 */
	@PostAuthorize("returnObject.applicant.email == principal.username || hasAuthority('" + APPLICATION_FORM_TRANSIT + "')")
	ApplicationForm findById(long id);
	
	/**
	 * 查阅某申请单关联的操作记录
	 * @param id
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	List<ApplicationHandleHistory> findHistoryByApplicationFormId(long id);
	
	/**
	 * 当出现懒加载异常时，重新开辟事务访问底层数据
	 * @param id
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	List<ApplicationHandleHistory> findByApplicationFormIdWhenException(long id);
	
	/**
	 * 模糊查询申请单列表
	 * @param name
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Page<ApplicationForm> findByNameLike(String name, @NotNull Pageable pageable);
	
	/**
	 * 根据申请单状态查找申请单
	 * @param status
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Page<ApplicationForm> findByStatus(@NotNull Status status, @NotNull Pageable pageable);
	
	/**
	 * 根据申请单状态查找申请单
	 * @param status
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
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
	 * 改变申请单状态，从安全上下文中获取处理人实体
	 * 注意：实现需记录处理的历史记录
	 * @param id
	 * @param status
	 * @param cause
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_TRANSIT + "')")
	void transit(@NotNull Long id, @NotNull Status status, String cause);
	
	/**
	 * 查询某个申请单的处理历史记录
	 * @param id
	 * @return
	 */
	@PreAuthorize("isAuthenticated() && hasAuthority('" + APPLICATION_FORM_READ_HISTORY + "')")
	ApplicationHandleHistory getHistoryById(long id);
	
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
	Page<ApplicationHandleHistory> history(String applicantEmail, String handlerEmail, String name, Status status, Date start, Date end, @NotNull Pageable pageable);

	/**
	 * 业务中被认证的name就是用户的email
	 * @return 一定不为null
	 */
	default String getEmail() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a != null)
			return a.getName();
		else
			return "";
	}

	/**
	 * 提供在特殊情况下的删除功能，包括历史记录，用户相关记录将被一并删除
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + APPLICATION_FORM_DELETE + "')")
	void delete(long id);
}
