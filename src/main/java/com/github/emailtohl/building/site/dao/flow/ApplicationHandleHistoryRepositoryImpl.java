package com.github.emailtohl.building.site.dao.flow;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.AbstractCriterionQueryRepository;
import com.github.emailtohl.building.site.entities.flow.ApplicationHandleHistory;
/**
 * 只需继承AbstractCriterionQueryRepository即可获得动态查询能力
 * @author HeLei
 * @date 2017.02.04
 */
public class ApplicationHandleHistoryRepositoryImpl extends AbstractCriterionQueryRepository<ApplicationHandleHistory>
		implements ApplicationHandleHistoryRepositoryCustomization {
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	/**
	 * 当发生上下文加载异常时，使用本接口获取历史操作记录
	 * @param id
	 * @return
	 */
	@Override
	public List<ApplicationHandleHistory> findByApplicationFormIdWhenException(long id) {
		List<ApplicationHandleHistory> ls = null;
		// String jpql = "SELECT h FROM ApplicationHandleHistory h WHERE h.applicationForm.id = ?1";
		EntityManager em = emf.createEntityManager();
		try {
			CriteriaBuilder b = em.getCriteriaBuilder();
			CriteriaQuery<ApplicationHandleHistory> q = b.createQuery(ApplicationHandleHistory.class);
			Root<ApplicationHandleHistory> r = q.from(ApplicationHandleHistory.class);
			q.select(r).where(b.equal(r.get("applicationForm").get("id"), id));
			
			em.getTransaction().begin();
			ls = em.createQuery(q).getResultList();
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		if (ls == null) {
			return new ArrayList<ApplicationHandleHistory>();
		} else {
			return ls;
		}
	}
}
