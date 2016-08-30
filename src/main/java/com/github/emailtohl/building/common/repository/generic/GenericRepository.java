package com.github.emailtohl.building.common.repository.generic;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
/**
 * 通用数据仓库的接口：增删改查
 * @author HeLei
 *
 * @param <I>
 * @param <E>
 */
@Validated
@Transactional
public interface GenericRepository<I extends Serializable, E extends Serializable> {
	@NotNull
	Iterable<E> getAll();
	
	List<E> entities();
	
	E get(@NotNull I id);

	void add(@NotNull E entity);

	void update(@NotNull E entity);

	void remove(@NotNull E entity);

	void removeById(@NotNull I id);
	
}