# web-building项目

**Java JavaScript 业务框架 spring springmvc springsecurity springdata JPA angularjs1.×**

## 一、 说明
此项目被称为“building”，意指“不断完善”的项目，我将日常学习、工作中使用的或自己开发的工具、框架整合在一起，不仅是一个总结。同时也可以在此整合的框架上开发业务项目。本项目中主要用到的技术有：
#### Java
 - 容器：spring、spring mvc
 - 安全：spring security
 - 数据访问层：JPA，spring data
 - 其他：校验等
 
#### JavaScript
- bootstrap
- requirejs
- angularjs
- 其他组件：如select2,datepicker,ztree,codemirror等等

## 二、部署
### 1. 数据源
系统中存在3份数据源配置，要保持一致：
###(1) 位于src/main/resources/database.properties
该文件主要为开发测试提供数据源配置，生产环境中，为了避免内存泄漏，安全，不统一等问题，应该使用tomcat容器中的数据源，见(2)

###(2) tomcat的context.xml文件：
```xml
	<Resource name="jdbc/building" type="javax.sql.DataSource"
		maxActive="20" maxIdle="5" maxWait="10000" username="postgres"
		password="123456" driverClassName="org.postgresql.Driver"
		defaultTransactionIsolation="READ_COMMITTED"
		url="jdbc:postgresql://localhost:5432/building" />
```
> 此处配置的是容器的数据源，程序通过JNDI查询使用该数据源

###(3) 位于src/main/resources/META-INF/persistence.xml
执行测试用例时，由于不在容器环境中，所以使用LocalEntityManagerFactoryBean来管理实体工厂，该Bean需要读取META-INF/persistence.xml中的配置。
> 注意，此文件之所以没有放在src/test/resources中，是因为src/test/java/目录下有许多其他共测试的Entities，为避免把数据表弄混乱，所以它仍然存放在src/main/resources/下

### 2. 创建数据库
在src/test/java/下的com.github.emailtohl.building.initdb包中可以有两种方式让JPA提供者生成数据表，并填入初始化数据。

### 3. 后端配置
项目的servlet、filter、listener均通过com.github.emailtohl.building.bootstrap下的程序启动，并未配置在web.xml中

### 4. 前端配置
后端主要靠RequireJS进行统一的代码管理，统一配置在webapp/common/main.js中。
业务代码统一位于webapp/app/下
angular组织代码时，一般由3个文件组成：
- context.js：主要告诉RequireJS该模块下要加载哪些文件
- module.js：当RequireJS加载并执行完后，内存中就存在了angular管理的模块，并创建出新的angular模块
- router：使用ui-router对angular的模块进行整合

## 三、 持续开发
目前只是将框架搭建好了，业务代码还未添加，项目持续进行中……