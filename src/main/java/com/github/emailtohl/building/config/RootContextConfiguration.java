package com.github.emailtohl.building.config;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.HibernateValidator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.util.ErrorHandler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
/**
 * spring容器的配置类，它依赖数据源配置类和安全配置类
 * @author HeLei
 */
@Configuration
// 启动Aspect动态代理
@EnableAspectJAutoProxy
// 启动时间计划任务，spring在扫描类时，发现有@Scheduled注解的方法，即可定时执行该方法
@EnableScheduling
// 扫描包下的注解，将Bean纳入spring容器管理
@ComponentScan(basePackages = "com.github.emailtohl.building", excludeFilters = @ComponentScan.Filter({
		Controller.class, Configuration.class }))
// 代理功能时，如事务，安全等，proxyTargetClass = false 表示使用Java的动态代理
@EnableAsync(mode = AdviceMode.PROXY, proxyTargetClass = false, order = Ordered.HIGHEST_PRECEDENCE)
@Import({ DataSourceConfiguration.class, JPAConfiguration.class, SecurityConfiguration.class })
public class RootContextConfiguration
		implements SchedulingConfigurer, AsyncConfigurer, TransactionManagementConfigurer {
	public static final String PROFILE_DEVELPMENT = "develpment";
	public static final String PROFILE_QA = "qa";
	public static final String PROFILE_PRODUCTION = "production";
	
	private static final Logger log = LogManager.getLogger();
	private static final Logger schedulingLogger = LogManager.getLogger(log.getName() + ".[scheduling]");

	@Inject
	Environment env;
	
	@Inject
	@Named("dataSource")
	DataSource dataSource;
	
	@Inject
	@Named("jpaTransactionManager")
	PlatformTransactionManager jpaTransactionManager;

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public DataSourceTransactionManager transactionManagerForTest() {
		return new DataSourceTransactionManager(dataSource);
	}
	
	/**
	 * 默认情况下，Spring总是使用ID为annotationDrivenTransactionManager的事务管理器
	 * 若实现了TransactionManagementConfigurer接口，则可以自定义提供事务管理器
	 * 注意：如果没有实现接口TransactionManagementConfigurer，且事务管理器的名字不是默认的annotationDrivenTransactionManager，可在注解 @Transactional的value指定。
	 */
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return this.jpaTransactionManager;
	}
	
	/**
	 * 向Spring容器中注册任务执行执行器
	 */
	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		log.info("Setting up thread pool task scheduler with 20 threads.");
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(20);
		scheduler.setThreadNamePrefix("task-");
		scheduler.setAwaitTerminationSeconds(60);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setErrorHandler(new ErrorHandler() {
			@Override
			public void handleError(Throwable t) {
				log.error("Unknown error occurred while executing task.", t);
			}
		});
		scheduler.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				schedulingLogger.error("Execution of task {} was rejected for unknown reasons.", r);
			}
		});
		return scheduler;
	}

	/**
	 * 配置任务执行器，所以需要实现SchedulingConfigurer接口
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar registrar) {
		TaskScheduler scheduler = this.taskScheduler();
		log.info("Configuring scheduled method executor {}.", scheduler);
		registrar.setTaskScheduler(scheduler);
	}
	
	/**
	 * 配置异步执行器，所以需要实现AsyncConfigurer
	 * 应用程序中通常使用的是JDK提供的线程，如：
	 * ExecutorService = Executors.newCachedThreadPool();
	 * 不过这会启动额外的资源，为了让整个应用程序启动的线程在可控范围内，可以统一使用注册在Spring中的taskScheduler
	 */
	@Override
	public Executor getAsyncExecutor() {
		Executor executor = this.taskScheduler();
		log.info("Configuring asynchronous method executor {}.", executor);
		return executor;
	}

	/**
	 * 配置异步执行器的异常处理
	 */
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {
			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
				schedulingLogger.error("调用异步任务出错了, message : " + method, ex);
			}};
	}
	
	/**
	 * Spring可以为其管理的Bean提供统一校验的功能，遵循Java EE的Bean Validation规范
	 * 首先，在存放数据的POJOs对象（如JavaBeans-like，实体（entities）或表单（form））中注解校验的内容，
	 * 然后在spring管理的bean中，指明哪些方法参数、哪些Field字段需要校验。
	 * 
	 * 要让Spring具有校验能力，首先得找到校验器工厂，然后再从工厂中获取校验器
	 * Spring自带了一个LocalValidatorFactoryBean校验器工厂
	 * 它可同时支持javax.validation.Validator和org.springframework.validation.Validator两个接口
	 * 前者是Java EE规范的一个校验接口，后者是前者的门面，它不仅提供统一的报错机制，还可以应用于Spring MVC的验证中。
	 * @return
	 */
	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		/*
		 * LocalValidatorFactoryBean会自动在classpath下搜索Bean Validation的实现
		 * 我们主要用的实现是HibernateValidator，但若在JAVA EE容器里面有多个提供者就不可预测，故还是手动设置提供类
		 */
		validator.setProviderClass(HibernateValidator.class);
		return validator;
	}
	
	/**
	 * 从校验工厂中获取到真正的校验器
	 * MethodValidationPostProcessor会寻找标注了@org.springframework.validation.annotation.Validated
	 * 和@javax.validation.executable.ValidateOnExecution的类，并为其创建代理
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		processor.setValidator(this.localValidatorFactoryBean());
		return processor;
	}
	
	@Bean
	public Gson gson() {
		return new Gson();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public CloseableHttpClient acceptsUntrustedCertsHttpClient()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
		HttpClientBuilder builder = HttpClientBuilder.create();

		String proxyHost = env.getProperty("proxyHost");
		String proxyPort = env.getProperty("proxyPort");
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort.length() > 0) {
			builder.setProxy(new HttpHost(proxyHost, Integer.valueOf(proxyPort)));
		}

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		// setup a Trust Strategy that allows all certificates.
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				log.warn("以下请求被信任且接受： \n" + "X509Certificate : " + Arrays.deepToString(arg0) + "  " + arg1);
				return true;
			}
		}).build();
		builder.setSSLContext(sslContext);
		// don't check Hostnames, either.
		// -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if
		// you don't want to weaken
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		// here's the special part:
		// -- need to create an SSL Socket Factory, to use our weakened "trust
		// strategy";
		// -- and create a Registry, to register it.
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory)
				.build();
		// now, we create connection-manager using our Registry.
		// -- allows multi-threaded use
		PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connMgr.setMaxTotal(200);
		connMgr.setDefaultMaxPerRoute(100);
		builder.setConnectionManager(connMgr);
		// finally, build the HttpClient;
		// -- done!
		CloseableHttpClient client = builder.build();
		return client;
	}

	/**
	 * RestTemplate默认使用的StringHttpMessageConverter采用ISO-8859-1编码，所以对中文支持不友好，这里需要替换为UTF-8
	 * 注意： List<HttpMessageConverter<?>>中的各个元素顺序不能变，例如StringHttpMessageConverter在第二个位置，只能原地替换
	 */
	private void reInitMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		HttpMessageConverter<?> converterTarget = null;
		for (HttpMessageConverter<?> item : converterList) {
			if (item.getClass() == StringHttpMessageConverter.class) {
				converterTarget = item;
				break;
			}
		}
		if (converterTarget != null) {
			Collections.replaceAll(converterList, converterTarget, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		}
	}

	@Bean
	public RestTemplate acceptsUntrustedCertsRestTemplate()
			throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, IOException {
		CloseableHttpClient httpClient = acceptsUntrustedCertsHttpClient();
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		reInitMessageConverter(restTemplate);
		return restTemplate;
	}
	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(env.getProperty("mailserver.host"));
		mailSender.setPort(Integer.valueOf(env.getProperty("mailserver.port")));
		mailSender.setUsername(env.getProperty("mailserver.username"));
		mailSender.setPassword(env.getProperty("mailserver.password"));
		Properties p = new Properties();
		p.setProperty("mail.debug", "true");
		String proxyHost = env.getProperty("proxyHost");
		String proxyPort = env.getProperty("proxyPort");
		String auth = env.getProperty("mailserver.auth");
		
		// 暂未找到通过代理发送邮件的方法
		if (proxyHost != null && !proxyHost.isEmpty()) {
		}
		if (proxyPort != null && !proxyPort.isEmpty()) {
		}
		if (auth != null && !auth.isEmpty()) {
			p.setProperty("mail.smtp.auth", auth);
		}
		mailSender.setJavaMailProperties(p);
		return mailSender;
	}
	
}
