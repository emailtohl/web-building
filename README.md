# web-building项目

**Java JavaScript 业务框架 spring springmvc springsecurity springdata JPA Hibernate angularjs1.×**

## 一、 说明
此项目被称为“building”，意指“不断完善”的项目，我将日常学习、工作中使用的或自己开发的工具、框架整合在一起，不仅是一个总结。同时也可以在此整合的框架上开发业务项目。技术选型上尽量符合业界标准，本项目中主要用到的技术有：
#### Java
 - 容器：spring、spring mvc
 - 安全：spring security
 - 数据访问层：JPA，spring data
 - 其他：校验等
 
#### JavaScript
- bootstrap
- requirejs
- angularjs
- 其他组件：如pager,modal,select2,datepicker,ztree,codemirror等等

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
> 注意，此文件之所以没有放在src/test/resources中，是因为src/test/java/目录下有许多其他供测试的Entities，为避免把数据表弄混乱，所以它仍然存放在src/main/resources/下

### 2. 创建数据库
在src/test/java/下的com.github.emailtohl.building.initdb包中可以有两种方式让JPA提供者生成数据表，并填入初始化数据，直接执行即可，该包下的CleanTestData可以将测试数据清除。

### 3. 后端配置
项目的servlet、filter、listener均通过com.github.emailtohl.building.bootstrap下的程序启动，并未配置在web.xml中

### 4. 前端配置
前端主要靠RequireJS进行统一的代码管理，统一配置在webapp/common/main.js中。
业务代码统一位于webapp/app/下
angular组织代码时，一般由3个文件组成：
- context.js：主要告诉RequireJS该模块下要加载哪些文件
- module.js：当RequireJS加载并执行完后，内存中就存在了angular管理的模块，并创建出新的angular模块
- router：使用ui-router对angular的模块进行整合

## 四、 如何阅读
### 1. 后端
#### 1.1 bootstrap包
有三个启动文件：他们继承了spring的适配器，容器（这里是tomcat）可以在初始化时调用他们的onStartup()方法，这三个文件会根据@Order注解顺序执行。

- ContainerBootstrap 该文件首先激活tomcat的默认servlet，使其过滤对静态文件的响应，之后创建spring的核心容器，并读取spring的生产配置，最后再注册Listener和Filter。

- SecurityBootstrap 在之前已经读取了spring security的配置后，该类向容器注册spring security的一系列过滤器，并执行初始化。

- FilterBootstrap 由于Filter加载有顺序之分，所以FilterBootstrap主要是最后需要注册的过滤器，它们最接近servlet，如压缩、JPA懒加载等过滤器。

#### 1.2 config包
- DataSourceConfiguration 根据spring依赖加载配置的顺序，DataSourceConfiguration应该是第一个被执行，它初始化最底层的数据源，由于@profile的配置，在单元测试时，spring会执行测试环境的配置，而在tomcat容器中，则会执行生产环境的配置。

- JPAConfiguration 它同样对测试和生产环境进行区分，将JPA交于spring来管理，这样当bean中含有@PersistenceContext注解时，spring会在每次线程执行时，注入一个实体管理器EntityManager。JPAConfiguration同样配置了spring data，如此一来，dao层的编码大多只在接口上声明即可，而无需重复写实现，当然特殊需求在源代码中也给出了优雅的解决方案。

- SecurityConfiguration 对spring security进行配置，spring security的管理的核心其实就是AuthenticationManager，本配置也示例出了自定义AuthenticationProvider的方法。注意，本配置还在静态内部类中声明@EnableGlobalMethodSecurity，如此一来，应用可以在脱离web应用时在方法级上进行安全保护。

- RootContextConfiguration 它将其他配置@Import进来，并注册了常用Bean，如线程管理器，Java标准校验，HttpClient，邮件客户端等实用Bean

- MvcConfiguration 不用多说，这是对Spring mvc的配置

#### 1.3 common包
作为公用组件，common包里面提供了自定义的一些实用工具，特别是JPA的BaseDao，封装了基本JPA的功能，并提供动态查询功能，进一步简化数据访问层的编码工作。此外utils中的BeanTools是自定义的一些分析JavaBean的工具，为我编写基础组件或框架所用。

#### 1.4 websocket包
主要对websocket的配置进行公共化，为业务代码提供读取HttpSession、用户认证和权限、对象序列化等支持

#### 1.5 site
这是编写业务代码的地方，其中entities中定义了JPA的基本对象，特别是BaseEntity，继承它的实体类，可获得基础功能，如创建时间、更新时间自动化

系统设计关键在于接口，是service包中不仅定义了接口功能，作为契约，接口还定义了校验、安全等切面功能，他们都由spring提供支持。

### 2. 前端
前端的lib存放第三方框架或工具，common中是我基于angular1.×编写的框架。
其中，directive中定义了日常开发中最常使用的分页Pager、select、文件上传、日期选择等指令，可以在模板页面中通用，具体可以在common/test中见使用方法。

common/service里面主要提到interceptors.js，这是一个面向切面的拦截器，在提交AJAX请求时，它提供统一的CSRF令牌，前后端页码转换，日志等功能。

前端的app文件夹下存放业务代码，它们根据模块进行划分，至于模块如何加载，它们依靠RequireJS的依赖关系，分别定义在每个模块下的context.js文件中，至于module.js和router.js它们分别是angular创建逻辑模块以及路由关系配置的文件。

### 3. 详情
README仅简述了项目概况，该项目是我学习所用，如何配置和运行的都详细注释在源文件中，可在项目中查看项目运行实际情况。

## 五、 持续开发
目前框架搭建以及基础组件以及创建完毕：如对用户、授权的实体建模，基础数据访问仓库，通用工具，通用过滤器，通用组件等，前端的拦截器、分页、select2等angular指令也都已齐备。

现在业务代码方面已添加了简单的用户管理功能，项目持续进行中……
