# web-building项目

**Java JavaScript 业务框架 spring springmvc springsecurity springdata JPA Hibernate search angularjs1.×**

## 一、 说明
此项目被称为“building”，意指“不断完善”的项目，我将日常学习或自己开发的工具、框架整合在一起。这不仅是一个总结，同时还可以在此基础上开发业务项目。

本项目在技术选型上尽量符合业界标准，主要使用的技术有：
#### Java
 - 容器：spring、spring mvc、Websocket
 - 安全：spring security
 - 数据访问层：JPA Hibernate、spring data
 - 其他：Hibernate校验、Hibernate Search（全文搜索）、lucence等
 
#### JavaScript
- bootstrap
- RequireJS
- AngularJS 1.×
- 其他组件：如select2,datepicker,ztree,codemirror等等

## 二、部署
本项目基于JDK8环境开发，框架部分为了兼容之前的JDK并未用JDK8的语法和新的时间类，但site包下的业务程序则需要调整才能兼容之前的JDK版本。
### 1. 数据源
由于属于学习研究型项目，所以本系统中存在3份数据源配置，要保持一致（真实生产环境可以简化为一份）：
###(1) 位于src/main/resources/database.properties
该文件主要为开发阶段提供简易的数据源，生产环境中，为了避免内存泄漏，安全，不统一等问题，应该在JavaEE容器中查找，例如tomcat的数据源配置，见(2)

###(2) tomcat的context.xml文件：
```xml
	<Resource name="jdbc/building" type="javax.sql.DataSource"
		maxActive="20" maxIdle="5" maxWait="10000" username="postgres"
		password="123456" driverClassName="org.postgresql.Driver"
		defaultTransactionIsolation="READ_COMMITTED"
		url="jdbc:postgresql://localhost:5432/building" />
```

此处配置的是容器的数据源，程序通过JNDI查询使用该数据源，在spring环境中使用非常简便：

```java
JndiDataSourceLookup lookup = new JndiDataSourceLookup();
return lookup.getDataSource("jdbc/building");
```

###(3) 位于src/main/resources/META-INF/persistence.xml
执行测试用例时，由于不在容器环境中，所以使用Spring的LocalEntityManagerFactoryBean来管理实体工厂，该Bean需要读取META-INF/persistence.xml中的配置。

> 注意，此文件之所以没有放在src/test/resources中，是因为src/test/java/目录下有许多其他供测试的Entities，为避免把数据表弄混乱，所以它仍然存放在src/main/resources/下

### 2. 创建数据库
使用JPA Hibernate开发项目的好处之一，就是可以以面向对象的视角来设计数据库表结构，在src/test/java/下的com.github.emailtohl.building.initdb包中可以有两种方式让JPA提供者（这里是Hibernate）生成数据表，并填入初始化的测试数据。它们是带main函数的类，执行后即可创建好数据库表结构，该包下的CleanTestData可以将测试数据清除。

### 3. 后端配置
xml的DTD、scheme校验很繁琐，项目尽可能避免使用xml，在配置上倾向于编程式的风格，servlet、filter、listener均通过com.github.emailtohl.building.bootstrap下的程序启动，并未配置在web.xml中

### 4. 前端配置
前端主要靠RequireJS统一管理代码，配置在webapp/common/main.js中。
业务代码统一位于webapp/app/下
逻辑模块则是由Angular组织，每个Angular模块一般由3个文件组成：
- context.js：主要告诉RequireJS该模块下要加载哪些js文件
- module.js：当RequireJS加载并执行完后，内存中就创建出了Angular管理的模块
- router：使用ui-router对Angular的模块进行整合

## 四、进入项目
### 1. 导入项目
项目基于maven构建，先导出为eclipse项目，当依赖包完全下载好后，根据前面介绍配置好本地数据库，本项目使用的数据库是postgresql，若要改为MySQL，则需要进com.github.emailtohl.building.config.JPAConfiguration中配置。

### 2. 初始化数据库
完成数据库配置后，执行src/test/java/com.github.emailtohl.building.initdb.CreateTable1，JPA Hibernate会自动创建数据表和初始的测试数据，创建的数据见com.github.emailtohl.building.initdb.PersistenceData，可自行创建自己的账户

### 3. 登录系统
打开浏览器连接到系统后，可进入首页，当访问授权地址时要求用户登录，用户可使用com.github.emailtohl.building.initdb.PersistenceData中配置的测试账户登录，也可以在登录页面中注册新的账户。

> 注意：注册用户会根据用户的邮箱发激活邮件，邮箱的配置在src/main/resources/config.properties中

登录成功后，对账户授权，最后根据用户的权限展示出不同的功能。

## 五、 如何阅读
### 1. 后端
#### 1.1 bootstrap包
有三个启动文件：他们继承了spring的适配器，容器（这里是tomcat）可以在初始化时调用他们的onStartup()方法，这三个文件会根据@Order注解顺序执行。

- ContainerBootstrap 该文件首先激活tomcat的默认servlet，使其过滤对静态文件的响应，然后创建spring的核心容器，并激活spring的生产配置，最后再向容器中注册Listener和Filter。

- SecurityBootstrap 向容器注册spring security的一系列过滤器，执行安全方面的初始化。

- FilterBootstrap 由于Filter加载有顺序之分，所以FilterBootstrap根据顺序注册Filter，，这些Filter离执行的servlet最近，如压缩、JPA懒加载等过滤器。

#### 1.2 config包
- DataSourceConfiguration 根据spring依赖加载配置的顺序，DataSourceConfiguration是第一个被执行，它初始化最底层的数据源，由于@profile的配置，在单元测试时，spring会读取测试环境的配置，而在tomcat容器中运行时，spring则会读取生产环境的配置。

- JPAConfiguration 它同样对测试和生产环境进行区分，该配置将JPA交于spring来管理，这样当bean中含有@PersistenceContext注解时，spring会在每次线程执行时，向bean注入一个新的实体管理器EntityManager。JPAConfiguration还配置了spring data，如此一来，大大减少了dao层的编码——仅仅声明接口无需编写实现，当然若有编写实现的需求时，可参看本项目中在源代码中UserRepositoryImpl的解决方案。

- SecurityConfiguration 对spring security进行配置，不仅为web层提供安全保护，同时也为应用程序service接口层提供安全保护。实际上，spring security的管理的核心其实就是AuthenticationManager，本配置为自定义AuthenticationProvider提供了示例。

- RootContextConfiguration 它将其他配置@Import进来，并注册了常用Bean，如线程管理器，Java标准校验，HttpClient，邮件客户端等实用Bean

- MvcConfiguration 不用多说，这是对Spring mvc的配置，并在ContainerBootstrap中读取

- WebsocketConfiguration 由于Websocket的应用程序受容器（如Tomcat）直接管理和调用，本配置可将其纳入Spring容器管理，并接受Spring的依赖注入

#### 1.3 common包
作为公用组件，common包里面提供了自定义的一些实用工具，特别是JPA的BaseDao，封装了基本JPA的功能，并提供动态查询功能，进一步简化数据访问层的编码工作。此外utils中的BeanTools是自定义的一些分析JavaBean的工具，为我编写基础组件或框架所用。

#### 1.4 websocket包
主要对websocket的配置进行公共化，为业务代码提供读取HttpSession、用户认证和权限、对象序列化等支持

#### 1.5 site
这是编写业务代码的地方，其中entities中定义了JPA的基本对象，特别是BaseEntity，继承它的实体类，可获得基础功能，如创建时间、更新时间自动化

系统设计关键在于接口，是service包中不仅定义了接口功能，作为契约，接口还定义了校验、安全等切面功能，他们都由spring提供支持。

### 2. 前端
前端的lib存放第三方框架或工具，common是公共模块，基于angular1.×编写的service、util、directive等。
其中，directive中定义了日常开发中最常使用的分页Pager、select、文件上传、日期选择等指令，可以在模板页面中通用，具体可以在common/test中见使用方法。

common/service里面主要提到interceptors.js，这是一个面向切面的拦截器，在提交AJAX请求时，它提供统一的CSRF令牌，前后端页码转换，日志等功能。

前端的app文件夹下存放业务代码，它们根据模块进行划分，至于模块如何加载，它们依靠RequireJS的依赖关系，分别定义在每个模块下的context.js文件中，至于module.js和router.js它们分别是angular创建逻辑模块以及路由关系配置的文件。

### 3. 详情
README仅简述了项目概况，该项目是我学习所用，如何配置和运行的都详细注释在源文件中，可在项目中查看项目运行实际情况。

## 六、 持续开发
目前框架搭建以及基础组件以及创建完毕：如对用户、授权的实体建模，基础数据访问仓库，通用工具，通用过滤器，通用组件等，前端的拦截器、分页、select2等angular指令也都已齐备。

现在业务代码方面基本完成用户管理，用户认证与授权，论坛全文搜索，工单流程申请，项目持续进行中……
