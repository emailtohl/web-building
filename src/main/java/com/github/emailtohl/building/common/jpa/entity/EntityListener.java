package com.github.emailtohl.building.common.jpa.entity;

import java.util.Date;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 创建日期、修改日期处理
 * 
 * @author HeLei
 * @date 2017.02.04
 */
public class EntityListener {
	private static final Logger log = LogManager.getLogger();

	/**
	 * 保存前处理
	 * 
	 * @param entity 基类
	 */
	@PrePersist
	public void prePersist(BaseEntity entity) {
		entity.setCreateDate(new Date());
		entity.setModifyDate(new Date());
	}

	/**
	 * 更新前处理
	 * 
	 * @param entity 基类
	 */
	@PreUpdate
	public void preUpdate(BaseEntity entity) {
		entity.setModifyDate(new Date());
	}

	@PostLoad
	void readTrigger(BaseEntity entity) {
		log.debug("entity read.");
	}

	@PostPersist
	void afterInsertTrigger(BaseEntity entity) {
		log.debug("entity inserted into database.");
	}

	@PostUpdate
	void afterUpdateTrigger(BaseEntity entity) {
		log.debug("entity just updated in the database.");
	}

	@PreRemove
	void beforeDeleteTrigger(BaseEntity entity) {
		log.debug("entity about to be deleted.");
	}

	@PostRemove
	void afterDeleteTrigger(BaseEntity entity) {
		log.debug("entity about deleted from database.");
	}
}
