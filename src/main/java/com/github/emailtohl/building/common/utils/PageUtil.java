package com.github.emailtohl.building.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.github.emailtohl.building.common.repository.jpa.Pager;

public final class PageUtil {
	public static <T> Pager<T> toPager(Page<T> p) {
		Pager<T> pager = new Pager<T>();
		pager.setDataList(p.getContent());
		pager.setPageNum((long) p.getNumber() + 1);
		pager.setPageSize(p.getSize());
		pager.setTotalPage((long) p.getTotalPages());
		pager.setTotalRow(p.getTotalElements());
		return pager;
	}
	
	public static <T> Page<T> toPager(Pager<T> p) {
		return new PageImpl<T>(p.getDataList());
	}
	
}
