package com.github.emailtohl.building.common.repository.jpa;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.AccessType;

import com.github.emailtohl.building.common.repository.generic.GenericRepository;
/**
 * 业务代码的数据仓库接口，可除了继承的增删改查以外，新增获取Pager对象的接口
 * @author HeLei
 * @param <E>
 */
public interface Repository<E extends Serializable> extends GenericRepository<Long, E> {
	Pager<E> getPager(String jpql, Object[] args, Integer pageNum, Integer pageSize);
	Pager<E> getPager(String jpql, Map<String, Object> args, Integer pageNum, Integer pageSize);
	Pager<E> getPager(E entity, Integer pageNum, Integer pageSize, AccessType type);
}
