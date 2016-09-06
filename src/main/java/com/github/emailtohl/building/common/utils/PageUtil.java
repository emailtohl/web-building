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
		return new Pager<T>(p.getContent(), p.getTotalElements(), p.getSize());
	}
	
	public static <T> Page<T> toPage(Pager<T> p) {
		return new PageImpl<T>(p.getContent());
	}
	
	public static <T> Page<T> toPage(Pager<T> p, Pageable pageable, long total) {
		return new PageImpl<T>(p.getContent(), pageable, total);
	}
}
