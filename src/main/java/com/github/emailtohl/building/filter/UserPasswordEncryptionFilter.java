package com.github.emailtohl.building.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.emailtohl.building.common.encryption.myrsa.Encipher;
/**
 * 考虑到集群环境部署，RSA的公钥和私钥没有直接放入ServletContext供整个应用使用？而是存放在了Session中。
 * 过滤器初始化时生成RSA的公钥和私钥，然后公钥交个各个客户端用于加密用户密码，私钥在服务端用于解密
 * 
 * Servlet Filter implementation class UserPasswordFilter
 * 
 * @author HeLei
 * @date 2017.02.04
 */
//@WebFilter("/login")
public class UserPasswordEncryptionFilter implements Filter {
	public static final ThreadLocal<HttpSession> CONCURRENT_SESSION = new ThreadLocal<HttpSession>();
	public static final String PUBLIC_KEY_PROPERTY_NAME = "publicKey";
	public static final String PRIVATE_KEY_PROPERTY_NAME = "privateKey";
	Encipher encipher = new Encipher();
	private String publicKey;
	private String privateKey;

    /**
     * Default constructor. 
     */
    public UserPasswordEncryptionFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 考虑到集群化部署，将系统计算的RSA公钥和私钥保存在session上，公钥发给前端私钥用于后端解密
		// 另外，放在Spring Security之后是为了不影响Spring Security对HttpSession的操作
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session.getAttribute(PUBLIC_KEY_PROPERTY_NAME) == null) {
			session.setAttribute(PUBLIC_KEY_PROPERTY_NAME, publicKey);
			session.setAttribute(PRIVATE_KEY_PROPERTY_NAME, privateKey);
		}
		try {
			CONCURRENT_SESSION.set(session);
			chain.doFilter(request, response);
		} finally {
			CONCURRENT_SESSION.remove();
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		String[] keyPairs = encipher.getKeyPairs(1024);
		publicKey = keyPairs[0];
		privateKey = keyPairs[1];
	}

}
