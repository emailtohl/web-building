#迁移环境时的注意事项

## 一、数据源
系统中存在3份数据源配置，要保持一致：
###1. 位于src/main/resources/database.properties
该文件主要为开发测试提供数据源配置，生产环境中，为了避免内存泄漏，安全，不统一等问题，应该使用tomcat容器中的数据源，见2

###2. tomcat的context.xml文件：
```xml
	<Resource name="jdbc/building" type="javax.sql.DataSource"
		maxActive="20" maxIdle="5" maxWait="10000" username="postgres"
		password="123456" driverClassName="org.postgresql.Driver"
		defaultTransactionIsolation="READ_COMMITTED"
		url="jdbc:postgresql://localhost:5432/building" />
```
> 此处配置的是容器的数据源，程序通过JNDI查询使用该数据源

###3. 位于src/main/resources/META-INF/persistence.xml
执行测试用例时，由于不在容器环境中，所以使用LocalEntityManagerFactoryBean来管理实体工厂，该Bean需要读取META-INF/persistence.xml中的配置。
> 注意，此文件之所以没有放在src/test/resources中，是因为src/test/java/目录下有许多其他共测试的Entities，为避免把数据表弄混乱，所以它仍然存放在src/main/resources/下

## 二、项目的部署
###1. 创建数据库
在src/test/java/下的com.github.emailtohl.building.initdb包中可以有两种方式让JPA提供者生成数据表，并填入初始化数据。

###2. 项目的配置
项目的servlet、filter、listener均通过com.github.emailtohl.building.bootstrap下的程序启动，并未配置在web.xml中


## 三、关于单元测试
查看单元测试覆盖率可以在项目根目录下运行如下命令:mvn cobertura:cobertura
常用命令

查看cobertura插件的帮助
mvn cobertura:help

清空cobertura插件运行结果
mvn cobertura:clean

运行cobertura的检查任务     
mvn cobertura:check      

在target文件夹下出现了一个site目录，下面是一个静态站点，里面就是单元测试的覆盖率报告。

## 四、关于Spring Security內建表达式说明
|             表达式                              |                说明                                                                                             |
| ------------------------- |:------------------------------------------------:|
| hasRole([role])           | 返回 true 如果当前主体拥有特定角色。                                                                      |
| hasAnyRole([role1,role2]) |返回 true 如果当前主体拥有任何一个提供的角色 （使用逗号分隔的字符串队列）|
| principal                 | 允许直接访问主体对象，表示当前用户                                                                              |
| authentication            | 允许直接访问当前 Authentication对象从SecurityContext中获得  |
| permitAll                 | 一直返回true                                       |
| denyAll                   | 一直返回false                                      |
| isAnonymous()             | 如果用户是一个匿名登录的用户 就会返回 true                    |
| isRememberMe()            | 如果用户是通过remember-me 登录的用户 就会返回 true           |
| isAuthenticated()         | 如果用户不是匿名用户就会返回true                          |
| isFullyAuthenticated()    | 如果用户不是通过匿名也不是通过remember-me登录的用户时， 就会返回true|
