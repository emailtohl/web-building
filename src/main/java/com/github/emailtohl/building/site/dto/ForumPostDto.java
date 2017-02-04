package com.github.emailtohl.building.site.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import com.github.emailtohl.building.common.Constant;
/**
 * 为满足Hibernate Search，论坛实体中含有User实体，而User实体中有些信息是不能被暴露在接口中
 * 故用ForumPostDto作为数据传输对象
 * @author HeLei
 * @date 2017.02.04
 */
public class ForumPostDto implements Serializable {
	private static final long serialVersionUID = 981676092735989648L;
	private Long id;
	private String username;
	@Pattern(regexp = Constant.PATTERN_EMAIL, flags = { Pattern.Flag.CASE_INSENSITIVE })
	private String email;
	private String title;
	private String body;
	private String keywords;
	private UserDto user;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public UserDto getUser() {
		return user;
	}
	public void setUser(UserDto user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "ForumPostDto [username=" + username + ", email=" + email + ", title=" + title + ", body=" + body
				+ ", keywords=" + keywords + ", user=" + user + "]";
	}
	
}
