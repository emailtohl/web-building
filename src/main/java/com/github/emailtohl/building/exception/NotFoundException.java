package com.github.emailtohl.building.exception;
/**
 * 应用缓存策略时，@Cacheable，会将接口返回的结果缓存下来，由于底层使用ConcurrentHashMap，所以不能为null
 * 所以若未找到资源，可抛异常表示。
 * 
 * @author HeLei
 * @date 2017.03.07
 */
public class NotFoundException extends Exception {
	private static final long serialVersionUID = 751559867915750446L;
	
	public NotFoundException(String string) {
		super(string);
	}
}
