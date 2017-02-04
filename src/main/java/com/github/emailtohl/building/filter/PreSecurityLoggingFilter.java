package com.github.emailtohl.building.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.util.WebUtils;
/**
 * 在spring security过滤器之前执行
 * Servlet Filter implementation class PreSecurityLoggingFilter
 * @author HeLei
 * @date 2017.02.04
 */
//@WebFilter("/*")
@SuppressWarnings("unused")
public class PreSecurityLoggingFilter implements Filter {
	public static final String ID_PROPERTY_NAME = "id";
	public static final String REQUEST_ID_PROPERTY_NAME = "Request-Id";
	public static final String SESSION_ID_PROPERTY_NAME = "sessionId";
	public static final String REMOTE_ADDRESS_PROPERTY_NAME = "remoteAddress";
	public static final String USER_PRINCIPAL_PROPERTY_NAME = "userPrincipal";
	
	/**
	 * Default constructor.
	 */
	public PreSecurityLoggingFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String id = UUID.randomUUID().toString();
		ThreadContext.put(ID_PROPERTY_NAME, id);
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			((HttpServletResponse) response).setHeader(REQUEST_ID_PROPERTY_NAME, id);
			ThreadContext.put(REMOTE_ADDRESS_PROPERTY_NAME, req.getRemoteAddr());
			ThreadContext.put(SESSION_ID_PROPERTY_NAME, req.getRequestedSessionId());
			ThreadContext.put(USER_PRINCIPAL_PROPERTY_NAME, req.getUserPrincipal() == null ? "" : req.getUserPrincipal().toString());
			chain.doFilter(request, response);
		} finally {
			ThreadContext.remove(ID_PROPERTY_NAME);
			ThreadContext.remove(REQUEST_ID_PROPERTY_NAME);
			ThreadContext.remove(REMOTE_ADDRESS_PROPERTY_NAME);
			ThreadContext.remove(SESSION_ID_PROPERTY_NAME);
			ThreadContext.remove(USER_PRINCIPAL_PROPERTY_NAME);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
