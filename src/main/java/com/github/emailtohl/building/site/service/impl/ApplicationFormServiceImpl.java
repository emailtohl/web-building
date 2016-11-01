package com.github.emailtohl.building.site.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion;
import com.github.emailtohl.building.site.dao.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.ApplicationHandleHistoryRepository;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.ApplicationFormService;
import com.github.emailtohl.building.site.service.UserService;
/**
 * 申请表业务流程实现类
 * @author HeLei
 */
@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject @Named("userServiceImpl") UserService userService;
	
	@Override
	public Long application(ApplicationForm applicationForm) {
		User u = userService.getUserByEmail(getEmail());
		applicationForm.setApplicant(u);
		applicationFormRepository.save(applicationForm);
		return applicationForm.getId();
	}

	@Override
	public ApplicationForm findById(long id) {
		return applicationFormRepository.findById(id);
	}

	@Override
	public Page<ApplicationForm> findByNameLike(String name, Pageable pageable) {
		if (isEmpty(name)) {
			return applicationFormRepository.findAll(pageable);
		}
		String fuzzy = name.trim() + '%';
		return applicationFormRepository.findByNameLike(fuzzy, pageable);
	}

	@Override
	public Page<ApplicationForm> findByStatus(Status status, Pageable pageable) {
		return applicationFormRepository.findByStatus(status, pageable);
	}

	@Override
	public Page<ApplicationForm> findByNameAndStatus(String name, Status status, Pageable pageable) {
		if (isEmpty(name)) {
			if (status == null) {
				return applicationFormRepository.findAll(pageable);
			} else {
				return applicationFormRepository.findByStatus(status, pageable);
			}
		} else {
			if (status == null) {
				return applicationFormRepository.findByNameLike(name.trim() + '%', pageable);
			} else {
				return applicationFormRepository.findByStatus(status, pageable);
			}
		}
	}
	
	@Override
	public Page<ApplicationForm> findMyApplicationForm(Pageable pageable) {
		String applicantEmail = getEmail();
		if (applicantEmail.isEmpty())
			return new PageImpl<>(new ArrayList<>());
		else
			return applicationFormRepository.findByApplicantEmailLike(applicantEmail, pageable);
	}

	@Override
	public void transit(Long id, Status status, String cause) {
		ApplicationForm af = applicationFormRepository.findOne(id);
		if (af.getStatus() == Status.COMPLETION) {
			throw new IllegalArgumentException("最终状态不能更改");
		}
		af.setStatus(status);
		af.setCause(cause);
		
		User u = userService.getUserByEmail(getEmail());
		ApplicationHandleHistory history = new ApplicationHandleHistory();
		history.setApplicationForm(af);
		history.setHandler(u);
		history.setStatus(status);
		history.setCause(cause);
		applicationHandleHistoryRepository.save(history);
		
		af.getApplicationHandleHistory().add(history);
	}

	@Override
	public ApplicationHandleHistory getHistoryById(long id) {
		return applicationHandleHistoryRepository.findOne(id);
	}
	
	@Override
	public Page<ApplicationHandleHistory> historyFindByCreateDateBetween(Date start, Date end, Pageable pageable) {
		return applicationHandleHistoryRepository.findByCreateDateBetween(start, end, pageable);
	}

	@Override
	public Page<ApplicationHandleHistory> historyFindByCreateDateGreaterThanEqual(Date date, Pageable pageable) {
		return applicationHandleHistoryRepository.findByCreateDateGreaterThanEqual(date, pageable);
	}

	@Override
	public Page<ApplicationHandleHistory> historyFindByCreateDateLessThanEqual(Date date, Pageable pageable) {
		return applicationHandleHistoryRepository.findByCreateDateLessThanEqual(date, pageable);
	}

	@Override
	public Page<ApplicationHandleHistory> historyFindByHandlerEmailLike(String email, Pageable pageable) {
		return applicationHandleHistoryRepository.findByHandlerEmailLike(email, pageable);
	}

	@Override
	public Page<ApplicationHandleHistory> historyFindByStatus(Status status, Pageable pageable) {
		return applicationHandleHistoryRepository.findByStatus(status, pageable);
	}

	/**
	 * 业务中被认证的name就是用户的email
	 * @return 一定不为null
	 */
	private String getEmail() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a != null)
			return a.getName();
		else
			return "";
	}

	@Override
	public Page<ApplicationHandleHistory> history(String applicantEmail, String handlerEmail, Status status, Date start, Date end, Pageable pageable) {
		Page<ApplicationHandleHistory> page;
		List<Criterion> ls = new ArrayList<>();
		if (!isEmpty(applicantEmail)) {
			ls.add(new Criterion("applicationForm.applicant.email", Criterion.Operator.LIKE, applicantEmail.trim() + '%'));
		}
		if (!isEmpty(handlerEmail)) {
			ls.add(new Criterion("handler.email", Criterion.Operator.LIKE, handlerEmail.trim() + '%'));
		}
		if (status != null) {
			ls.add(new Criterion("status", Criterion.Operator.EQ, status));
		}
		if (start != null) {
			ls.add(new Criterion("applicationForm.createDate", Criterion.Operator.GTE, start));
		}
		if (end != null) {
			ls.add(new Criterion("applicationForm.createDate", Criterion.Operator.LTE, end));
		}
		page = applicationHandleHistoryRepository.search(ls, pageable);
		return page;
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

}
