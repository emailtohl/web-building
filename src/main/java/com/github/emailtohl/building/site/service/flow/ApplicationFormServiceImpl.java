package com.github.emailtohl.building.site.service.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion;
import com.github.emailtohl.building.site.dao.flow.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.flow.ApplicationHandleHistoryRepository;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm;
import com.github.emailtohl.building.site.entities.flow.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm.Status;
import com.github.emailtohl.building.site.entities.user.User;
/**
 * 申请表业务流程实现类
 * @author HeLei
 * @date 2017.02.04
 */
@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject UserRepository userRepository;
	
	@Override
	public Long application(String name, String description) {
		User u = userRepository.findByEmail(getEmail());
		ApplicationForm af = new ApplicationForm(u, name, description);
		applicationFormRepository.save(af);
		return af.getId();
	}

	@Override
	public ApplicationForm findById(long id) {
		ApplicationForm af = applicationFormRepository.findById(id);
		af.getApplicationHandleHistory().size();// 初始化集合
		return af;
	}

	@Override
	public List<ApplicationHandleHistory> findHistoryByApplicationFormId(long id) {
		return applicationHandleHistoryRepository.findByApplicationFormId(id);
	}
	
	@Override
	public Page<ApplicationForm> findByNameLike(String name, Pageable pageable) {
		if (!StringUtils.hasText(name)) {
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
		if (!StringUtils.hasText(name)) {
			if (status == null) {
				return applicationFormRepository.findAll(pageable);
			} else {
				return applicationFormRepository.findByStatus(status, pageable);
			}
		} else {
			if (status == null) {
				return applicationFormRepository.findByNameLike(name.trim() + '%', pageable);
			} else {
				return applicationFormRepository.findByNameLikeAndStatus(name.trim() + '%', status, pageable);
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
		
		User u = userRepository.findByEmail(getEmail());
		
		ApplicationHandleHistory history = new ApplicationHandleHistory();
		history.setApplicationForm(af);
		history.setHandler(u);
		history.setStatus(status);
		history.setCause(cause);
		applicationHandleHistoryRepository.save(history);
		
		af.setHandler(u);
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


	@Override
	public Page<ApplicationHandleHistory> history(String applicantEmail, String handlerEmail, String name, Status status, Date start, Date end, Pageable pageable) {
		Page<ApplicationHandleHistory> page;
		Set<Criterion> set = new HashSet<>();
		if (StringUtils.hasText(applicantEmail)) {
			set.add(new Criterion("applicationForm.applicant.email", Criterion.Operator.LIKE, applicantEmail.trim() + '%'));
		}
		if (StringUtils.hasText(handlerEmail)) {
			set.add(new Criterion("handler.email", Criterion.Operator.LIKE, handlerEmail.trim() + '%'));
		}
		if (StringUtils.hasText(name)) {
			set.add(new Criterion("applicationForm.name", Criterion.Operator.LIKE, name.trim() + '%'));
		}
		if (status != null) {
			set.add(new Criterion("status", Criterion.Operator.EQ, status));
		}
		if (start != null) {
			set.add(new Criterion("applicationForm.createDate", Criterion.Operator.GTE, start));
		}
		if (end != null) {
			set.add(new Criterion("applicationForm.createDate", Criterion.Operator.LTE, end));
		}
		page = applicationHandleHistoryRepository.search(set, pageable);
		return page;
	}
	
	@Override
	public List<ApplicationHandleHistory> findByApplicationFormIdWhenException(long id) {
		return applicationHandleHistoryRepository.findByApplicationFormIdWhenException(id);
	}

	@Override
	public void delete(long id) {
		ApplicationForm af = applicationFormRepository.findOne(id);
		applicationHandleHistoryRepository.deleteInBatch(af.getApplicationHandleHistory());
		applicationFormRepository.delete(id);
	}

}
