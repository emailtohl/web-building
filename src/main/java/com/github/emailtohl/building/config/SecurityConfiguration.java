package com.github.emailtohl.building.config;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
/**
 * spring security 的编程风格的配置，它依赖于数据源配置类
 * @author HeLei
 */
@Profile(PROFILE_PRODUCTION)
@Configuration
// 启动安全过滤器
@EnableWebSecurity
@Import({ DataSourceConfiguration.class })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Inject
	@Named("dataSource")
	DataSource dataSource;
	
	/**
	 * 外部可以使用它，从而获取到身份信息
	 * @return SessionRegistry
	 */
	@Bean
	protected SessionRegistry sessionRegistryImpl() {
		return new SessionRegistryImpl();
	}

	/**
	 * 配置用户、角色、权限的来源
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		/*
		builder.inMemoryAuthentication().withUser("emailtohl@163.com").password("123456").authorities("USER", "ADMIN")
				.and().withUser("test").password("1").authorities("USER", "ADMIN")
				.and().withUser("foo@test.com").password("123456").authorities("USER")
				.and().withUser("bar@test.com").password("123456").authorities("USER");
		*/
		
		builder.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("SELECT t.email as username, t.password, t.enabled FROM t_user AS t WHERE t.email = ?")
				.authoritiesByUsernameQuery("SELECT u.email AS username, ua.authority FROM t_user u INNER JOIN t_user_authority ua ON u.id = ua.user_id WHERE u.email = ?")
				.passwordEncoder(new BCryptPasswordEncoder());
		
	}
	/**
	 * 默认忽略的路径
	 */
	@Override
	public void configure(WebSecurity security) {
		security.ignoring().antMatchers("/lib/**");
		security.ignoring().antMatchers("/common/**");
		security.ignoring().antMatchers("/app/**");
		security.ignoring().antMatchers("/download/**");
	}
	/**
	 * 细化各类路径的安全配置
	 */
	@Override
	protected void configure(HttpSecurity security) throws Exception {
		security
				.authorizeRequests()
					.antMatchers("/"/* 首页 */, "login"/* 获取登录页面 */, "/authenticate"/* 登录时的认证 */, "/authentication"/* 获取认证信息 */, "/index.html", "/home.html", "/signup", "/about", "/chat/**").permitAll()
					.antMatchers("/admin/**").hasAuthority("ADMIN")
					.antMatchers("/secure/**").hasAnyAuthority("ADMIN", "MANAGER")
					.antMatchers(HttpMethod.DELETE, "/user/**").hasAnyAuthority("ADMIN", "MANAGER")
					.antMatchers(HttpMethod.POST, "/user/**").hasAnyAuthority("ADMIN", "MANAGER")
					.antMatchers(HttpMethod.PUT, "/user/**").hasAnyAuthority("ADMIN", "MANAGER")
					.anyRequest().authenticated()
				// HTTP Basic Authentication是基于REST风格，通过HTTP状态码与访问它的应用程序进行沟通
				/*.and().httpBasic()*/
				// 登录配置
				.and().formLogin()
					.loginPage("/login").failureUrl("/login?error").defaultSuccessUrl("/")
					.usernameParameter("email").passwordParameter("password").permitAll()
				// 登出配置，注意：Spring security在启动CSRF时，默认只使用HTTP POST，这是为了确保注销需要CSRF令牌和恶意用户不能强行注销你的用户
				// 虽然不推荐，但如果一定要使用<a>标签链接，get等方式退出，则必须更新下面的Java配置：logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.and().logout()
					/*.logoutUrl("/logout")*/
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?loggedOut").invalidateHttpSession(true)
					.deleteCookies("JSESSIONID").permitAll()
				// session管理，例如登录后切换sessionid，只允许一个人一处登录
				.and().sessionManagement()
					.sessionFixation().changeSessionId().maximumSessions(1).maxSessionsPreventsLogin(true)
					.sessionRegistry(sessionRegistryImpl())
				.and().and().csrf()/*.disable()*/.csrfTokenRepository(csrfTokenRepository())
				.and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
				// rememberMe默认的过期时间是2周，这里设为四周；默认的私钥名是SpringSecured，这里设为"building"
				.rememberMe().tokenValiditySeconds(2419200).key("building");
		
	}
	
	/**
	 * 如果是基于表单的方式提交请求，则只需要在表单中添加一个含有令牌的隐藏域即可，这里的令牌是通过服务器返回的
	 * 如JSP中通过name="${_csrf.parameterName}" value="${_csrf.token}"获取令牌
	 * 但如果是通过程序访问服务器，如其他REST客户程序，Angular前端应用程序访问服务器的话，则不能每次在服务器那里拿到令牌
	 * 这时可以通过访问cookie的方式获取令牌，而CsrfHeaderFilter会将CSRF令牌保存在cookie中
	 * 此外CsrfHeaderFilter需要在Spring security过滤链之后，这样才能访问得到CsrfToken
	 */
	class CsrfHeaderFilter extends OncePerRequestFilter {
		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
				throws ServletException, IOException {
			CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrf != null) {
				Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
				String token = csrf.getToken();
				if (cookie == null || token != null && !token.equals(cookie.getValue())) {
					cookie = new Cookie("XSRF-TOKEN", token);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * 另外对于CSRF中还需要做的一件事是告诉Spring security期望返回CSRF令牌的头名叫做“X-XRSF-TOKEN”而不是默认的“X-CSRF-TOKEN”
	 * @return
	 */
	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}
	
	/**
	 * 默认的WebSecurityConfigurerAdapter没有暴露AuthenticationManager Bean
	 * 若在应用程序中使用AuthenticationManager，则可以将其注册进spring容器中
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	/**
	 * 为了在应用层而非过滤器中使用spring security，还可以启动@EnableGlobalMethodSecurity功能
	 * 这时候spring会在调用Bean方法时再添加一个切面，执行spring security的安全检查
	 * 由于@EnableGlobalMethodSecurity是注解在class上的，而本class已经继承了WebSecurityConfigurerAdapter，所以只能新建一个配置类
	 * AuthorizationConfiguration作为静态的，带@Configuration的内部类，可以被spring识别，并导入到本配置类中
	 */
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true, order = 0, mode = AdviceMode.PROXY, proxyTargetClass = false)
	public static class AuthorizationConfiguration extends GlobalMethodSecurityConfiguration {}
	
}