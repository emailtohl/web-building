package com.github.emailtohl.building.site.service;

import static com.github.emailtohl.building.site.entities.Authority.USER_CREATE_SPECIAL;
import static com.github.emailtohl.building.site.entities.Authority.USER_DELETE;
import static com.github.emailtohl.building.site.entities.Authority.USER_DISABLE;
import static com.github.emailtohl.building.site.entities.Authority.USER_GRANT_ROLES;
import static com.github.emailtohl.building.site.entities.Authority.USER_READ_ALL;
import static com.github.emailtohl.building.site.entities.Authority.USER_READ_SELF;
import static com.github.emailtohl.building.site.entities.Authority.USER_UPDATE_ALL;
import static com.github.emailtohl.building.site.entities.Authority.USER_UPDATE_SELF;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.User;

/**
 * 用户管理的服务
 * 
 * @author HeLei
 */
@Transactional
@Validated
public interface UserService {

	/**
	 * 创建雇员账号
	 * 注意：对于Spring Security来说，新增用户时，必须同时为其添加相应的用户授权，否则即便激活了该用户，也不会让其登录
	 * @param u
	 * @return
	 */
	@PreAuthorize("hasAuthority('" + USER_CREATE_SPECIAL + "')")
	Long addEmployee(@Valid Employee u);
	
	/**
	 * 注册普通账号，无需权限即可
	 * 注意：对于Spring Security来说，新增用户时，必须同时为其添加相应的用户授权，否则即便激活了该用户，也不会让其登录
	 * @param u
	 * @return
	 */
	Long addCustomer(@Valid Customer u);
	
	/**
	 * 启用用户，无需权限即可
	 * @param id
	 */
	void enableUser(@Min(value = 1L) Long id);
	
	/**
	 * 禁用用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + USER_DISABLE + "')")
	void disableUser(@Min(value = 1L) Long id);
	
	/**
	 * 授予用户角色
	 * @param roleNames
	 */
	@PreAuthorize("hasAuthority('" + USER_GRANT_ROLES + "')")
	void grantRoles(long id, String... roleNames);
	
	/**
	 * 修改密码，限制只能本人才能修改
	 * 登录页面中通过邮箱方式修改密码在AuthenticationService接口中
	 * authentication是直接从SecurityContextHolder中获取的对象
	 * @param id
	 * @param newPassword
	 */
	@PreAuthorize("#email == authentication.principal.username")
	void changePassword(@P("email") String email, @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 修改密码，用于用户忘记密码的场景，没有权限控制
	 * 由方法内部逻辑判断进行修改
	 * @param email
	 * @param newPassword
	 */
	void changePasswordByEmail(String email, @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 删除用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + USER_DELETE + "')")
	void deleteUser(@Min(value = 1L) Long id);
	
	/**
	 * 查询用户，通过认证的均可调用
	 * returnObject和principal是spring security内置对象
	 * @param id
	 * @return
	 */
	@PostAuthorize("hasAuthority('" + USER_READ_ALL + "') || (hasAuthority('" + USER_READ_SELF + "') && returnObject.username == principal.username)")
	User getUser(@Min(value = 1L) Long id);
	
	/**
	 * 通过邮箱名查询用户，通过认证的均可调用
	 * 
	 * @param email
	 * @return
	 */
	@PostAuthorize("hasAuthority('" + USER_READ_ALL + "') || (hasAuthority('" + USER_READ_SELF + "') && #email == principal.username)")
	User getUserByEmail(@NotNull String email);
	
	/**
	 * 修改用户
	 * 这里的方法名使用的是merge，传入的User参数只存储需要更新的属性，不更新的属性值为null
	 * 
	 * 修改密码，启用/禁用账户，授权功能，不走此接口
	 * 
	 * @param u中的id不能为null， u中属性不为null的值为修改项
	 */
	@PreAuthorize("hasAuthority('" + USER_UPDATE_ALL + "') || (hasAuthority('" + USER_UPDATE_SELF + "') && #email == principal.username)")
	void mergeUser(@NotNull @P("email") String email, User u/* 用最大范围接收数据 */);
	
	/**
	 * 获取用户Pager
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	@PreAuthorize("isAuthenticated()")
	Pager<User> getUserPager(User u, Pageable pageable);
	
	/**
	 * 获取用户Page,这里的Page是Spring Data提供的数据结构
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	@PreAuthorize("isAuthenticated()")
	Page<User> getUserPage(User u, Pageable pageable);
	
}
