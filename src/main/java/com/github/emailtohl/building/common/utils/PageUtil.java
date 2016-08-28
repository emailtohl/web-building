package com.github.emailtohl.building.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.repository.jpa.Pager;
/**
 * 自己的Pager对象和Spring提供的Page对象之间的转换
 * @author HeLei
 */
public final class PageUtil {
	public static <T> Pager<T> toPager(Page<T> p) {
		Pager<T> pager = new Pager<T>();
		pager.setDataList(p.getContent());
		pager.setPageNum(p.getNumber() + 1);
		pager.setPageSize(p.getSize());
		pager.setTotalPage(p.getTotalPages());
		pager.setTotalRow(p.getTotalElements());
		return pager;
	}
	
	public static <T> Page<T> toPage(Pager<T> p) {
		return new PageImpl<T>(p.getDataList());
	}
	
	public static <T> Page<T> toPage(Pager<T> p, Pageable pageable, long total) {
		return new PageImpl<T>(p.getDataList(), pageable, total);
	}
}
