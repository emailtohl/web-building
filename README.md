# web-building项目

**Java JavaScript 业务框架 spring springmvc springsecurity springdata JPA Hibernate search Envers Lucene angularjs1.× AdminLTE**

## 一、 说明
此项目被称为“building”，意指“不断完善”的项目，我将日常学习或自己开发的工具、框架整合在一起。这不仅是一个总结，同时还可以在此基础上开发业务项目。

本项目在技术选型上尽量符合业界标准，主要使用的技术有：
#### Java
 - 容器：spring、spring mvc、Websocket、spring Publish-Subscribe（消息订阅）
 - 安全：spring security（XSS、CSRF、Session固定、应用层安全校验）
 - 数据访问层：JPA Hibernate、spring data、lucene（搜索引擎）
 - 其他：Hibernate校验、Hibernate Envers（审计）、Hibernate Search（全文搜索）等
 
#### JavaScript
- bootstrap + adminLTE
- RequireJS
- AngularJS 1.×
- 其他组件：如select2,modal,datepicker,ztree,codemirror等等

## 二、部署
本项目基于JDK8+tomcat9环境开发，common包中的框架部分代码使用传统的JDK6语法，使之可应用得更广泛，而site包下的业务程序则大量使用JDK8的语法，如Lambda表达式，方法引用，Stream流式风格以及新的时间类。

> 注意：由于本项目监控和聊天功能使用websocket这种长连接技术，所以部署在集群环境下websocket只能转到固定服务器上，若与登录时的服务器不同，前端浏览器的sessionId就会被更新，使得登录状态失效。

> 事实上websocket是改变规则的技术，它让服务器与浏览器、服务器与服务器之间（代码中有示例）通信更实时，完全可以自定义集群之间的数据共享，可参考com.github.emailtohl.building.message包中建立在websocket之上的自定义集群通信技术(4.1.7)。

### 2.1 数据源
由于属于学习研究型项目，所以本系统中存在3份数据源配置，要保持一致（真实生产环境可以简化为一份）：
#### (1) 位于src/main/resources/database.properties
该文件主要为开发阶段提供简易的数据源，生产环境中，为了避免内存泄漏，安全，不统一等问题，应该在JavaEE容器中查找，例如tomcat的数据源配置，见(2)

#### (2) tomcat的context.xml文件：
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

#### (3) 位于src/main/resources/META-INF/persistence.xml
该文件实际上是JPA规范所要求的，不过由于引入了Spring的LocalEntityManagerFactoryBean来管理实体工厂，此文件可以省略。为了保留参考，我将此文件应用到测试环境下，此文件之所以没有放在src/test/resources中，是因为src/test/java/目录下有许多其他供测试的Entities，为避免Hibernate自动更新把数据表弄混乱，所以它仍然存放在src/main/resources/下。


### 2.2 创建数据库
系统分析，数据建模是软件开发中最重要的环节，使用JPA Hibernate开发项目的好处之一，就是可以以面向对象的视角来设计数据库表结构，在src/test/java/下的com.github.emailtohl.building.initdb包中可以有两种方式让Hibernate生成数据表，并填入初始化的测试数据。它们是带main函数的类，执行后即可创建好数据库表结构，该包下的CleanTestData可以将测试数据清除。

### 2.3 后端配置
xml的DTD、scheme校验很繁琐，项目尽可能避免使用xml，在配置上倾向于编程式的风格，servlet、filter、listener均通过com.github.emailtohl.building.bootstrap下的程序启动，并未配置在web.xml中

### 2.4 前端配置
前端主要靠RequireJS统一管理代码，配置在webapp/common/main.js中。
业务代码统一位于webapp/app/下
逻辑模块则是由Angular组织，每个Angular模块一般由3个文件组成：
- context.js：主要告诉RequireJS该模块下要加载哪些js文件
- module.js：当RequireJS加载并执行完后，内存中就创建出了Angular管理的模块
- router：使用ui-router对Angular的模块进行整合

## 三、进入项目
### 3.1 导入项目
项目基于maven构建，先导出为eclipse项目，当依赖包完全下载好后，根据前面介绍配置好本地数据库，本项目使用的数据库是postgresql，若要改为MySQL，则需要进com.github.emailtohl.building.config.JPAConfiguration中配置。

### 3.2 初始化数据库
完成数据库配置后，执行src/test/java/com.github.emailtohl.building.initdb.CreateTable1，JPA Hibernate会自动创建数据表和初始的测试数据，创建的数据见com.github.emailtohl.building.initdb.PersistenceData，可自行创建自己的账户

### 3.3 登录系统
打开浏览器连接到系统后，可进入首页，当访问授权地址时要求用户登录，用户可使用超级管理员账号是：emailtohl@163.com/123456登录，系统中还预存了几个测试账户，他们统一定义在com.github.emailtohl.building.initdb.PersistenceData中，也可以在登录页面中注册新的账户。

> 注意：注册用户会根据用户的邮箱发激活邮件，邮箱的配置在src/main/resources/config.properties中

登录成功后，对账户授权，最后根据用户的权限展示出不同的功能。

## 四、 如何阅读
### 4.1 后端
#### 4.1.1 bootstrap包
有三个启动文件：他们继承了spring的适配器，容器（这里是tomcat）可以在初始化时调用他们的onStartup()方法，这三个文件会根据@Order注解顺序执行。

- ContainerBootstrap 该文件首先激活tomcat的默认servlet，使其过滤对静态文件的响应，然后创建spring的核心容器，并激活spring的生产配置，最后再向容器中注册Listener和Filter。

- SecurityBootstrap 向容器注册spring security的一系列过滤器，执行安全方面的初始化。

- FilterBootstrap 由于Filter加载有顺序之分，所以FilterBootstrap根据顺序注册Filter，这些Filter距离servlet最近，如压缩、JPA懒加载等过滤器。

#### 4.1.2 config包
- DataSourceConfiguration 根据spring依赖加载配置的顺序，DataSourceConfiguration是第一个被执行，它初始化最底层的数据源，并且定义外部存储目录。由于@profile的配置，在单元测试时，spring会读取测试环境的配置，而在tomcat容器中运行时，spring则会读取生产环境的配置。

- JPAConfiguration 它同样对测试和生产环境进行区分，该配置将JPA交于spring来管理，这样当bean中含有@PersistenceContext注解时，spring会在每次线程执行时，向bean注入一个新的实体管理器EntityManager。JPAConfiguration还配置了spring data，如此一来，大大减少了dao层的编码——仅仅声明接口无需编写实现，当然若有编写实现的需求时，可参看本项目中在源代码中UserRepositoryImpl的解决方案。

- SecurityConfiguration 对spring security进行配置，不仅为web层提供安全保护，同时也为应用程序service接口层提供安全保护。实际上，spring security的管理的核心其实就是AuthenticationManager，com.github.emailtohl.building.site.service.user.UserServiceImpl提供了自定义AuthenticationProvider示例。

- RootContextConfiguration 它将其他配置@Import进来，并注册了常用Bean，如线程管理器，Java标准校验，HttpClient，邮件客户端等实用Bean

- MvcConfiguration 不用多说，这是对Spring mvc的配置，并在ContainerBootstrap中读取

- WebsocketConfiguration 由于Websocket的应用程序受容器（如Tomcat）直接管理和调用，本配置可将其纳入Spring容器管理，并接受Spring的依赖注入

#### 4.1.3 common包
作为公用组件，common包里面提供了自定义的一些实用工具，特别是JPA的BaseDao，封装了基本JPA的功能，并提供动态查询、全文搜索、审计查询功能，进一步简化数据访问层的编码工作。此外utils中的BeanTools是自定义的一些分析JavaBean的工具，为我编写基础组件或框架所用。

#### 4.1.4 websocket包
主要对websocket的配置进行公共化，为业务代码提供读取HttpSession、用户认证和权限、对象序列化等支持

#### 4.1.5 site
这是编写业务代码的地方，其中entities中定义了JPA的基本对象，特别是BaseEntity，继承它的实体类，可获得基础功能，如创建时间、更新时间自动化

系统设计关键在于接口，是service包中不仅定义了接口功能，作为契约，接口还定义了校验、安全等切面功能，他们都由spring提供支持。

#### 4.1.6 exception包
利用Spring @ControllerAdvice注解，统一拦截并处理异常。

#### 4.1.7 message包
该包使用了Spring-context包中的消息发布-订阅(Publish-Subscribe)技术，能很好地解决观察者模式的紧耦合问题，利用该技术再结合websocket，可使集群环境下各服务端通过广播地址创建连接，从而发布集群消息。

1. 首先创建出继承ApplicationEvent的ClusterEvent，将原java.util.EventObject中瞬时的source改为持久化；

2. 关注ClusterManager，当spring的上下文初始化或刷新时，会触发ContextRefreshedEvent，这时候就发起连接到本服务地址上；

3. 经过短暂的响应后ClusterManager就会将自身的地址通过socket发到广播地址上；

4. ClusterManager的listener属性是一个线程，它也使用socket（基于TCP双向收发消息）监听广播地址上的消息（没有消息时会在receive处阻塞）；

5. 当收到消息时，如果是自己的地址就忽略，否则就根据该消息创建一个websocket连接，并将该websocket连接注册到ClusterEventMulticaster中；

6. 一旦使用ApplicationEventPublisher#publishEvent(ClusterEvent event)，ClusterEventMulticaster的multicastEvent(ApplicationEvent event)就会广播该消息，不仅实现ApplicationListener<ClusterEvent>的类会收到，websocket中的各节点也会收到。

> 注意：端点的IP是通过InetAddress.getLocalHost().getHostAddress();获取，注意多个端点在同一网段中；此外，若端点的端口号不是8080，则需要配置config.properties文件中的local.host值。

#### 4.1.7 encryption包
该包下含有JDK标准RSA+AES实现：Crypter，不过由于密钥生成应该有客户端完成，故该类并未在项目中真正使用。不过该包中引入了自己实现RSA算法，这样就可以在底层和前端传递来的数字公钥进行交互处理了，现在主要应用加密用户的登录密码，可在普通的HTTP协议下保证登录安全。

#### 4.1.8 lucene包
该包是对lucene简单的封装，可支持本CMS模块对文件系统的索引和检索，由于lucene较为底层，数据源的获取、分词、检索、过滤、分析等等各个方面都需要优良算法的程序支持，高级封装可参考相应的开源软件，如Solr可提取数据库和XML信息；Nutch、Heritrix、Grub获取web站点；Aperture可支持web站点，文件系统、邮箱；Tika能提供数据过滤等等。

### 4.2 前端
前端的lib存放第三方框架或工具，common是公共模块，基于angular1.×编写的service、util、directive等。
其中，directive中定义了日常开发中最常使用的分页Pager、select、文件上传、日期选择等指令，可以在模板页面中通用，具体可以在common/test中见使用方法。

common/service里面主要提到interceptors.js，这是一个面向切面的拦截器，在提交AJAX请求时，它提供统一的CSRF令牌，前后端页码转换，日志等功能。

前端的app文件夹下存放业务代码，它们根据模块进行划分，至于模块如何加载，它们依靠RequireJS的依赖关系，分别定义在每个模块下的context.js文件中，至于module.js和router.js它们分别是angular创建逻辑模块以及路由关系配置的文件。

### 4.3 详情
README仅简述了项目概况，该项目是我学习所用，如何配置和运行的都详细注释在源文件中，可在项目中查看项目运行实际情况。

## 五、 项目状态
目前框架搭的通用基础功能基本完善，如：基础数据访问仓库，通用工具，通用过滤器，通用组件等，前端的拦截器、分页、select2、模态框等angular指令。

为验证通用技术，建立了如下几个示例业务模块：用户、授权实体建模，首页聊天室、实时数据监控、论坛全文搜索，工单流程申请，客户关系管理，内容管理，密钥管理等。

现项目基本完成可裁剪应用于普通项目中。
