# DEPRECATED

此项目已弃用，不再维护，请转至更新项目:[integration](https://github.com/emailtohl/integration)。
This project is deprecated and no longer being maintained, please switch to the update project: [integration](https://github.com/emailtohl/integration).


# web-building项目

**Java JavaScript 业务框架 spring springmvc springsecurity springdata JPA Hibernate search Envers Lucene angularjs1.× AdminLTE**


## 一、 说明
此项目被称为“building”，意指“不断完善”的项目，我将日常学习或自己开发的工具、框架整合在一起。这不仅是一个总结，同时还可以在此基础上开发业务项目。代码风格上，公共部分代码，如common包使用传统的JDK6语法，使之可应用得更广泛，而业务代码，如site包则大量使用JDK8的语法，如Lambda表达式，方法引用，Stream流式风格以及新的时间类。

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
本项目基于JDK8+tomcat9+postgresql环境开发，基于maven构建，先导出为eclipse项目，并等待下载所有依赖包（等待时间可能有些长）。

本项目使用websocket技术，最好使用与开发相同环境部署。容器方面，可以使用tomcat8，而数据库方面，得益于持久层使用的JPA技术，可很容易地切换（配置位于com.github.emailtohl.building.config.JPAConfiguration中配置）。

需要说明的是本项目中集成的websocket不适合于集群，这是因为websocket是双向长连接技术，与负载均衡中的Http分发原理不同。在集群环境下若websocket连接的服务器和http分发到的服务器不同，则sessionId会被刷新，使得登录状态失效。目前还未找到有效解决方案。

不过websocket是一门改变规则的技术，虽然离开了负载均衡器的Http转发这一传统模式，但我们可以使用websocket自定义集群环境，可参考com.github.emailtohl.building.message包中建立在websocket之上的自定义集群通信技术(3.7)。


### 2.1 数据源
由于属于学习研究型项目，项目中创建了3份数据源配置，而真实生产环境可以简化为一份：
#### 2.1.1 位于src/main/resources/database.properties
这是由应用程序管理的数据源，负责创建和关闭，主要用于开发测试阶段。而生产环境中，一般是获取容器提供的数据源，这样可以避免内存泄漏，安全等问题。

#### 2.1.2 tomcat的context.xml文件：
容器启动时，也是在生产环境中配置的数据源：

```xml
	<Resource name="jdbc/building" type="javax.sql.DataSource"
		maxActive="20" maxIdle="5" maxWait="10000" username="postgres"
		password="123456" driverClassName="org.postgresql.Driver"
		defaultTransactionIsolation="READ_COMMITTED"
		url="jdbc:postgresql://localhost:5432/building" />
```

程序通过JNDI查询使用该数据源，在spring环境中使用非常简便：

```java
	@Profile({ “production", "qa" })
	@Bean(name = "dataSource")
	public DataSource jndiDataSource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource("jdbc/building");
	}
```

#### 2.1.3 位于src/main/resources/META-INF/persistence.xml
该文件是JPA规范所要求的，Java EE容器会在META-INF目录下扫描该文件，不过我们采用的是Spring而非Java EE容器来管理JPA，所以可通过Spring的LocalContainerEntityManagerFactoryBean采用编程风格配置JPA，不再使用此文件。

不过，作为参考，我保留了此文件，并将此文件用于单元测试环境中，被Spring的LocalEntityManagerFactoryBean使用。需要说明的是，此文件之所以没有放在src/test/resources中，是因为src/test/java/目录下有许多其他供测试的Entities，为避免Hibernate自动更新功能将数据表弄混乱，所以它仍然存放在src/main/resources/下。


### 2.2 创建数据库
系统分析，数据建模是软件开发中最重要的环节，使用JPA Hibernate开发项目的好处之一，就是可以以面向对象的视角来设计数据库表结构，在src/test/java/下的com.github.emailtohl.building.initdb包中可以有两种方式让Hibernate生成数据表，并填入初始化的测试数据。它们是带main函数的类，执行后即可创建好数据库表结构，该包下的CleanTestData可以将测试数据清除。

部署时，数据库方面需要做的是：

(1) 在postgresql中创建名为building的数据库。

(2) 将数据库的地址、用户名、密码配置到src/main/resources/META-INF/persistence.xml中再执行src/test/java/下的com.github.emailtohl.building.initdb.CreateTable1.java或CreateTable2.java，他们执行的效果是一样的，所不同的是CreateTable1直接使用JPA机制执行，而CreateTable2则包装到Spring容器下执行。

> 注意：创建数据库的基础数据位于com.github.emailtohl.building.initdb.PersistenceData中，其中包括登录系统的账号，可自行对登录账号进行任意设置，包括管理员权限。项目也可以启动后注册，注册用户会根据用户的邮箱发激活邮件，邮箱的配置在src/main/resources/config.properties中，登录成功后，对账户授权，最后根据用户的权限展示出不同的功能。

### 2.3 tomcat容器配置
将项目部署到tomcat中，需要配置两个地方：

(1) 将数据库的地址、驱动、用户名和密码配置到到context.xml文件中，见(2.1.2)。

(2) 项目中的搜索引擎、cms等模块使用了文件系统，所以需要tomcat的虚拟空间，在server.xml的<host>标签下，添加如下如下配置

```xml
<Context debug="0" docBase="D:\program\apache-tomcat-9.0.0.M17\wtpwebapps\web-building-data\resource" path="/building/resource" reloadable="true"/>
```

若还需要在tomcat中配置Https或集群，也可以参考本文件同目录下的remark.md文档。

### 2.4 后端配置
xml的DTD、scheme校验很繁琐，且配置不够灵活，所以项目尽可能避免使用xml，更倾向于编程式的风格，servlet、filter、listener均通过com.github.emailtohl.building.bootstrap下的程序启动，并未配置在web.xml中。

### 2.5 前端配置
前端主要靠RequireJS统一管理代码，配置在webapp/common/main.js中。
业务代码统一位于webapp/app/下
逻辑模块则是由Angular组织，每个Angular模块一般由3个文件组成：
- context.js：主要告诉RequireJS该模块下要加载哪些js文件
- module.js：当RequireJS加载并执行完后，内存中就创建出了Angular管理的模块
- router：使用ui-router对Angular的模块进行整合


## 三、 后端代码
### 3.1 bootstrap包
有三个启动文件：他们继承了spring的适配器，容器（这里是tomcat）可以在初始化时调用他们的onStartup()方法，这三个文件会根据@Order注解顺序执行。

- ContainerBootstrap 该文件首先激活tomcat的默认servlet，使其过滤对静态文件的响应，然后创建spring的核心容器，并激活spring的生产配置，最后再向容器中注册Listener和Filter。

- SecurityBootstrap 向容器注册spring security的一系列过滤器，执行安全方面的初始化。

- FilterBootstrap 由于Filter加载有顺序之分，所以FilterBootstrap根据顺序注册Filter，这些Filter距离servlet最近，如压缩、JPA懒加载等过滤器。

### 3.2 config包
- DataSourceConfiguration 根据spring依赖加载配置的顺序，DataSourceConfiguration是第一个被执行，它初始化最底层的数据源，并且定义外部存储目录。由于@profile的配置，在单元测试时，spring会读取测试环境的配置，而在tomcat容器中运行时，spring则会读取生产环境的配置。

- JPAConfiguration 它同样对测试和生产环境进行区分，该配置将JPA交于spring来管理，这样当bean中含有@PersistenceContext注解时，spring会在每次线程执行时，向bean注入一个新的实体管理器EntityManager。JPAConfiguration还配置了spring data，如此一来，大大减少了dao层的编码——仅仅声明接口无需编写实现，当然若有编写实现的需求时，可参看本项目中在源代码中UserRepositoryImpl的解决方案。

- SecurityConfiguration 对spring security进行配置，不仅为web层提供安全保护，同时也为应用程序service接口层提供安全保护。实际上，spring security的管理的核心其实就是AuthenticationManager，com.github.emailtohl.building.site.service.user.UserServiceImpl提供了自定义AuthenticationProvider示例。

- RootContextConfiguration 它将其他配置@Import进来，并注册了常用Bean，如线程管理器，Java标准校验，HttpClient，邮件客户端等实用Bean。

- MvcConfiguration 不用多说，这是对Spring mvc的配置，并在ContainerBootstrap中读取。

- WebsocketConfiguration 由于Websocket的应用程序受容器（如Tomcat）直接管理和调用，本配置可将其纳入Spring容器管理，并接受Spring的依赖注入。

### 3.3 common包
作为公用组件，common包里面提供了自定义的一些实用工具，特别是JPA的BaseDao，封装了基本JPA的功能，并提供动态查询、全文搜索、审计查询功能，进一步简化数据访问层的编码工作。此外utils中的BeanTools是自定义的一些分析JavaBean的工具，为我编写基础组件或框架所用。

### 3.4 websocket包
主要对websocket的配置进行公共化，为业务代码提供读取HttpSession、用户认证和权限、对象序列化等支持

### 3.5 site
这是编写业务代码的地方，其中entities中定义了JPA的基本对象，特别是BaseEntity，继承它的实体类，可获得基础功能，如创建时间、更新时间自动化

系统设计关键在于接口，是service包中不仅定义了接口功能，作为契约，接口还定义了校验、安全等切面功能，他们都由spring提供支持。

### 3.6 exception包
利用Spring @ControllerAdvice注解，统一拦截并处理异常。

### 3.7 message包
该包使用了Spring-context包中的消息发布-订阅(Publish-Subscribe)技术，能很好地解决观察者模式的紧耦合问题，利用该技术再结合websocket，可使集群环境下各服务端通过广播地址创建连接，从而发布集群消息。

(1) 首先创建出继承ApplicationEvent的ClusterEvent，将原java.util.EventObject中瞬时的source改为持久化；

(2) 关注ClusterManager，当spring的上下文初始化或刷新时，会触发ContextRefreshedEvent，这时候就发起连接到本服务地址上；

(3) 经过短暂的响应后ClusterManager就会将自身的地址通过socket发到广播地址上；

(4) ClusterManager的listener属性是一个线程，它也使用socket（基于TCP双向收发消息）监听广播地址上的消息（没有消息时会在receive处阻塞）；

(5) 当收到消息时，如果是自己的地址就忽略，否则就根据该消息创建一个websocket连接，并将该websocket连接注册到ClusterEventMulticaster中；

(6) 一旦使用ApplicationEventPublisher#publishEvent(ClusterEvent event)，ClusterEventMulticaster的multicastEvent(ApplicationEvent event)就会广播该消息，不仅实现ApplicationListener<ClusterEvent>的类会收到，websocket中的各节点也会收到。

> 注意：端点的IP是通过InetAddress.getLocalHost().getHostAddress();获取，注意多个端点在同一网段中；此外，若端点的端口号不是8080，则需要配置config.properties文件中的local.host值。

### 3.8 encryption包
该包下含有JDK标准RSA+AES实现：Crypter，不过由于密钥生成应该有客户端完成，故该类并未在项目中真正使用。不过该包中引入了自己实现RSA算法，这样就可以在底层和前端传递来的数字公钥进行交互处理了，现在主要应用加密用户的登录密码，可在普通的HTTP协议下保证登录安全。

### 3.9 lucene包
该包是对lucene简单的封装，可支持本CMS模块对文件系统的索引和检索，由于lucene较为底层，数据源的获取、分词、检索、过滤、分析等等各个方面都需要优良算法的程序支持，高级封装可参考相应的开源软件，如Solr可提取数据库和XML信息；Nutch、Heritrix、Grub获取web站点；Aperture可支持web站点，文件系统、邮箱；Tika能提供数据过滤等等。

## 四、 前端代码
前端的lib存放第三方框架或工具，common是公共模块，基于angular1.×编写的service、util、directive等。
其中，directive中定义了日常开发中最常使用的分页Pager、select、文件上传、日期选择等指令，可以在模板页面中通用，具体可以在common/test中见使用方法。

common/service里面主要提到interceptors.js，这是一个面向切面的拦截器，在提交AJAX请求时，它提供统一的CSRF令牌，前后端页码转换，日志等功能。

前端的app文件夹下存放业务代码，它们根据模块进行划分，至于模块如何加载，它们依靠RequireJS的依赖关系，分别定义在每个模块下的context.js文件中，至于module.js和router.js它们分别是angular创建逻辑模块以及路由关系配置的文件。


## 五、 项目状态
目前项目已基本完成，通用基础功能基本完善，如：基础数据访问仓库，通用工具，通用过滤器，通用组件等，前端的拦截器、分页、select2、模态框等angular指令。

为验证通用技术，建立了如下几个示例业务模块：用户、授权实体建模，首页聊天室、实时数据监控、论坛全文搜索，工单流程申请，客户关系管理，内容管理，密钥管理等。

现项目基本完成可裁剪应用于普通项目中。

总的来说项目还是有些遗憾的如：

- Log4j2升级会导致不能正常关闭容器；

- spring security默认使用的是内置的用户认证和默认的重定向页面，并没有使用纯粹的RESTfull风格，这导致一些如前后端的异常处理、密码加密等编码并不优雅；

- 统一异常处理方式用得并不好；

- 过于追求Spring的Profile配置，导致数据库配置太多，也不易整改，以后可能会在测试代码中使用内存型数据库；

- 前端也有一些组件写得并不好，如select2的复选、文件上传（不如引入成熟组件）等等。

现在项目已具备规模，修改以上的问题可能会影响业务代码，再加上不是主要问题，所以也就这样吧，作为学习型项目，目的已经达到了。
