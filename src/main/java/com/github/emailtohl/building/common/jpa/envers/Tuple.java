package com.github.emailtohl.building.common.jpa.envers;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
/**
 * 对AuditQuery接口查出来的Object[]进行封装的对象
 * @author HeLei
 * @date 2017.02.04
 *
 * @param <E> 实体类型
 */
public class Tuple<E> {
	private E entity;
	private DefaultRevisionEntity defaultRevisionEntity;
	private RevisionType revisionType;
	
	public Tuple() {
		super();
	}
	
	public Tuple(E entity, DefaultRevisionEntity defaultRevisionEntity, RevisionType revisionType) {
		super();
		this.entity = entity;
		this.defaultRevisionEntity = defaultRevisionEntity;
		this.revisionType = revisionType;
	}

	public E getEntity() {
		return entity;
	}
	public void setEntity(E entity) {
		this.entity = entity;
	}
	public DefaultRevisionEntity getDefaultRevisionEntity() {
		return defaultRevisionEntity;
	}
	public void setDefaultRevisionEntity(DefaultRevisionEntity defaultRevisionEntity) {
		this.defaultRevisionEntity = defaultRevisionEntity;
	}
	public RevisionType getRevisionType() {
		return revisionType;
	}
	public void setRevisionType(RevisionType revisionType) {
		this.revisionType = revisionType;
	}

}
