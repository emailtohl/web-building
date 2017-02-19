package com.github.emailtohl.building.common.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 基于Spring Security上下文，判断用户授权的工具
 * @author HeLei
 * @date 2017.02.04
 */
public final class SecurityContextUtil {
	
	private SecurityContextUtil() {}
	
	/**
	 * 获取认证信息
	 * @return
	 */
	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	/**
	 * 获取用户名，若未获取，则返回null
	 * @return
	 */
	public static String getCurrentUsername() {
		Authentication a = getAuthentication();
		if (a != null)
			return a.getName();
		else
			return null;
	}
	
	/**
	 * 获取授权的字符串集合
	 * @return
	 */
	public static Set<String> getAuthorities() {
		Set<String> set = new HashSet<>();
		Authentication a = getAuthentication();
		if (a != null) {
			a.getAuthorities().forEach(g -> {
				set.add(g.getAuthority());
			});
		}
		return set;
	}
	
	/**
	 * 判断是否有参数中的授权
	 * @param authorities
	 * @return
	 */
	public static boolean hasAnyAuthority(String... authorities) {
		Authentication a = getAuthentication();
		if (a == null || a.getAuthorities() == null)
			return false;
		List<String> ls = Arrays.asList(authorities);
		for (GrantedAuthority g : a.getAuthorities()) {
			if (ls.contains(g.getAuthority()))
				return true;
		}
		return false;
	}
}
