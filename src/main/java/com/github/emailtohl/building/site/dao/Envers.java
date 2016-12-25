package com.github.emailtohl.building.site.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Repository;

import com.github.emailtohl.building.site.entities.ApplicationForm;

/**
 * 查看Hibernate Envers审计内容
 * 
 * @author HeLei
 */
@Repository
public class Envers {
	@Inject
	EntityManagerFactory entityManagerFactory;

	public void applicationForm() {
		EntityManager em = entityManagerFactory.createEntityManager();
		AuditReader auditReader = AuditReaderFactory.get(em);

		AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(ApplicationForm.class, false, false);
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		Long id = null;
		for (Object[] tuple : result) {
			ApplicationForm af = (ApplicationForm) tuple[0];
			id = af.getId();
			System.out.println(af);
			DefaultRevisionEntity revision = (DefaultRevisionEntity) tuple[1];
			System.out.println(revision);
			RevisionType revisionType = (RevisionType) tuple[2];
			System.out.println(revisionType);
			/*if (revision.getId() == 1) {
				assertEquals(revisionType, RevisionType.ADD);
				assertEquals(item.getName(), "Foo");
			} else if (revision.getId() == 2) {
				assertEquals(revisionType, RevisionType.MOD);
				assertEquals(item.getName(), "Bar");
			} else if (revision.getId() == 3) {
				assertEquals(revisionType, RevisionType.DEL);
				assertNull(item);
			}*/
		}

		// Number revisionCreate =
		// auditReader.getRevisionNumberForDate(TIMESTAMP_CREATE);
		// Number revisionUpdate =
		// auditReader.getRevisionNumberForDate(TIMESTAMP_UPDATE);
		// Number revisionDelete =
		// auditReader.getRevisionNumberForDate(TIMESTAMP_DELETE);
		List<Number> revisions = auditReader.getRevisions(ApplicationForm.class, id);
		revisions.size();
		for (Number revision : revisions) {
			Date revisionTimestamp = auditReader.getRevisionDate(revision);
			// ...
			System.out.println(revisionTimestamp);
			
			ApplicationForm ra = auditReader.find(ApplicationForm.class, id, revision);
			System.out.println(ra);
		}

	}

}
