# 部署与开发参考

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


## 三、tomcat虚拟目录的配置
在server.xml的<host>标签下，添加如下如下配置

```xml
<Context path="/building/icon_dir" docBase="D:\program\apache-tomcat-8.0.32\wtpwebapps\web-building-upload\icon_dir" reloadable="true" debug="0"/>
```

## 四、 HTTPS原理以及tomcat配置HTTPS方法
### 1. 什么是HTTPS

在说HTTPS之前先说说什么是HTTP，HTTP就是我们平时浏览网页时候使用的一种协议。

HTTP协议传输的数据都是未加密的，也就是明文的，因此使用HTTP协议传输隐私信息非常不安全。为了保证这些隐私数据能加密传输，于是网景公司设计了SSL（Secure Sockets Layer）协议用于对HTTP协议传输的数据进行加密，从而就诞生了HTTPS。

SSL目前的版本是3.0，被IETF（Internet Engineering Task Force）定义在RFC 6101中，之后IETF对SSL 3.0进行了升级，于是出现了TLS（Transport Layer Security） 1.0，定义在RFC 2246。

实际上我们现在的HTTPS都是用的TLS协议，但是由于SSL出现的时间比较早，并且依旧被现在浏览器所支持，因此SSL依然是HTTPS的代名词，但无论是TLS还是SSL都是上个世纪的事情，SSL最后一个版本是3.0，今后TLS将会继承SSL优良血统继续为我们进行加密服务。

目前TLS的版本是1.2，定义在RFC 5246中，暂时还没有被广泛的使用。

### 2. Https的工作原理
HTTPS在传输数据之前需要客户端（浏览器）与服务端（网站）之间进行一次握手，在握手过程中将确立双方加密传输数据的密码信息。TLS/SSL协议不仅仅是一套加密传输的协议，更是一件经过艺术家精心设计的艺术品，TLS/SSL中使用了非对称加密，对称加密以及HASH算法。握手过程的简单描述如下：

（1）浏览器将自己支持的一套加密规则发送给网站。

（2）网站从中选出一组加密算法与HASH算法，并将自己的身份信息以证书的形式发回给浏览器。证书里面包含了网站地址，加密公钥，以及证书的颁发机构等信息。

（3）获得网站证书之后浏览器要做以下工作：

a) 验证证书的合法性（颁发证书的机构是否合法，证书中包含的网站地址是否与正在访问的地址一致等），如果证书受信任，则浏览器栏里面会显示一个小锁头，否则会给出证书不受信的提示。

b) 如果证书受信任，或者是用户接受了不受信的证书，浏览器会生成一串随机数的密码，并用证书中提供的公钥加密。

c) 使用约定好的HASH计算握手消息，并使用生成的随机数对消息进行加密，最后将之前生成的所有信息发送给网站。

（4）网站接收浏览器发来的数据之后要做以下的操作：

a) 使用自己的私钥将信息解密取出密码，使用密码解密浏览器发来的握手消息，并验证HASH是否与浏览器发来的一致。

b) 使用密码加密一段握手消息，发送给浏览器。

（5）浏览器解密并计算握手消息的HASH，如果与服务端发来的HASH一致，此时握手过程结束，之后所有的通信数据将由之前浏览器生成的随机密码并利用对称加密算法进行加密。

这里浏览器与网站互相发送加密的握手消息并验证，目的是为了保证双方都获得了一致的密码，并且可以正常的加密解密数据，为后续真正数据的传输做一次测试。另外，HTTPS一般使用的加密与HASH算法如下：

非对称加密算法：RSA，DSA/DSS

对称加密算法：AES，RC4，3DES

HASH算法：MD5，SHA1，SHA256

其中非对称加密算法用于在握手过程中加密生成的密码，对称加密算法用于对真正传输的数据进行加密，而HASH算法用于验证数据的完整性。

由于浏览器生成的密码是整个数据加密的关键，因此在传输的时候使用了非对称加密算法对其加密。非对称加密算法会生成公钥和私钥，公钥只能用于加密数据，因此可以随意传输，而网站的私钥用于对数据进行解密，所以网站都会非常小心的保管自己的私钥，防止泄漏。

TLS握手过程中如果有任何错误，都会使加密连接断开，从而阻止了隐私信息的传输。

### 3. 为服务器生成证书
“运行”控制台，进入%JAVA_HOME%/bin目录，使用如下命令进入目录：
使用keytool为Tomcat生成证书，假定目标机器的域名是“localhost”，keystore文件存放在“D:\home\tomcat.keystore”，口令为“password”，使用如下命令生成：

```
keytool -genkey -v -alias tomcat -keyalg RSA -keystore D:\home\tomcat.keystore -validity 36500
```

> 参数简要说明：“D:\home\tomcat.keystore”含义是将证书文件的保存路径，证书文件名称是tomcat.keystore ；“-validity 36500”含义是证书有效期，36500表示100年，默认值是90天 “tomcat”为自定义证书名称

在命令行填写必要参数:

* 输入keystore密码：此处需要输入大于6个字符的字符串。

* “您的名字与姓氏是什么？”这是必填项，并且必须是TOMCAT部署主机的域名或者IP[如：gbcom.com 或者 10.1.25.251]（就是你将来要在浏览器中输入的访问地址），否则浏览器会弹出警告窗口，提示用户证书与所在域不匹配。在本地做开发测试时，应填入“localhost”。

* 你的组织单位名称是什么？”、“您的组织名称是什么？”、“您所在城市或区域名称是什么？”、“您所在的州或者省份名称是什么？”、“该单位的两字母国家代码是什么？”可以按照需要填写也可以不填写直接回车，在系统询问“正确吗？”时，对照输入信息，如果符合要求则使用键盘输入字母“y”，否则输入“n”重新填写上面的信息。

* 输入<tomcat>的主密码，这项较为重要，会在tomcat配置文件中使用，建议输入与keystore的密码一致，设置其它密码也可以，完成上述输入后，直接回车则在你在第二步中定义的位置找到生成的文件。

### 4. 为客户端生成证书
为浏览器生成证书，以便让服务器来验证它。为了能将证书顺利导入至IE和Firefox，证书格式应该是PKCS12，因此，使用如下命令生成：
```
keytool -genkey -v -alias mykey -keyalg RSA -storetype PKCS12 -keystore D:\home\mykey.p12 （mykey为自定义）。
```
对应的证书库存放在“D:\home\mykey.p12”，客户端的CN可以是任意值。双击mykey.p12文件，即可将证书导入至浏览器（客户端）。

### 5. 让服务器信任客户端证书
由于是双向SSL认证，服务器必须要信任客户端证书，因此，必须把客户端证书添加为服务器的信任认证。由于不能直接将PKCS12格式的证书库导入，必须先把客户端证书导出为一个单独的CER文件，使用如下命令：
```
keytool -export -alias mykey -keystore D:\home\mykey.p12 -storetype PKCS12 -storepass password -rfc -file D:\home\mykey.cer
```
mykey为自定义与客户端定义的mykey要一致，password是你设置的密码。通过以上命令，客户端证书就被我们导出到“D:\home\mykey.cer”文件了。

下一步，是将该文件导入到服务器的证书库，添加为一个信任证书使用命令如下：
```
keytool -import -v -file D:\home\mykey.cer -keystore D:\home\tomcat.keystore
```
通过list命令查看服务器的证书库，可以看到两个证书，一个是服务器证书，一个是受信任的客户端证书：
```
keytool -list -keystore D:\home\tomcat.keystore (tomcat为你设置服务器端的证书名)。
```

### 6. 让客户端信任服务器证书

由于是双向SSL认证，客户端也要验证服务器证书，因此，必须把服务器证书添加到浏览的“受信任的根证书颁发机构”。由于不能直接将keystore格式的证书库导入，必须先把服务器证书导出为一个单独的CER文件，使用如下命令：
```
keytool -keystore D:\home\tomcat.keystore -export -alias tomcat -file D:\home\tomcat.cer (tomcat为你设置服务器端的证书名)。
```
通过以上命令，服务器证书就被我们导出到“D:\home\tomcat.cer”文件了。双击tomcat.cer文件，按照提示安装证书，将证书填入到“受信任的根证书颁发机构”。

### 7. 配置Tomcat服务器
打开Tomcat根目录下的/conf/server.xml，找到Connector port="8443"配置段，修改为如下：

```xml
<Connector protocol="org.apache.coyote.http11.Http11NioProtocol"
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="D:\home\tomcat.keystore" keystorePass="password"
           clientAuth="false" sslProtocol="TLS"/>
```

> tomcat要与生成的服务端证书名一致

属性说明：

* clientAuth:设置是否双向验证，默认为false，设置为true代表双向验证

* keystoreFile:服务器证书文件路径

* keystorePass:服务器证书密码

* truststoreFile:用来验证客户端证书的根证书

* truststorePass:根证书密码

### 8. 测试
在浏览器中输入:https://localhost:8443/，会弹出选择客户端证书界面，点击“确定”，会进入tomcat主页，地址栏后会有“锁”图标，表示本次会话已经通过HTTPS双向验证，接下来的会话过程中所传输的信息都已经过SSL信息加密。

## 五、关于tomcat的集群配置

### 1. 应用程序提供集群支持

在应用的web.xml的<web-app></web-app>标签下添加<distributable/>标签，开启对集群的支持。

### 2. 开启tomcat的对集群的支持

在tomcat的server.xml的<Engine>标签中添加下面这段配置，（直接copy），配置具体含义见[tomcat官方的集群配置文档](http://tomcat.apache.org/tomcat-8.0-doc/cluster-howto.html)
(http://tomcat.apache.org/tomcat-9.0-doc/cluster-howto.html)

```xml

        <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"
                 channelSendOptions="8">

          <Manager className="org.apache.catalina.ha.session.DeltaManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"/>

          <Channel className="org.apache.catalina.tribes.group.GroupChannel">
            <Membership className="org.apache.catalina.tribes.membership.McastService"
						bind="127.0.0.1"
                        address="228.0.0.4"
                        port="45564"
                        frequency="500"
                        dropTime="3000"/>
            <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                      address="auto"
                      port="4000"
                      autoBind="100"
                      selectorTimeout="5000"
                      maxThreads="6"/>

            <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
              <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
            </Sender>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
            <!-- tomcat8 -->
            <!-- <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatch15Interceptor"/> -->
            <!-- tomcat9 -->
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor"/>
          </Channel>

          <Valve className="org.apache.catalina.ha.tcp.ReplicationValve"
                 filter=""/>
          <Valve className="org.apache.catalina.ha.session.JvmRouteBinderValve"/>

          <Deployer className="org.apache.catalina.ha.deploy.FarmWarDeployer"
                    tempDir="/tmp/war-temp/"
                    deployDir="/tmp/war-deploy/"
                    watchDir="/tmp/war-listen/"
                    watchEnabled="false"/>

          <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/>
        </Cluster>
        
```

> 注意：集群中tomcat间用组播方式进行通信，如果机器上有多个网卡（也可能是虚拟机的网卡）则可能导致组播失败，解决的办法是<Cluster>元素的<Membership>元素配置bind属性，它用于明确知道组播地址。

```xml
<Membership className="org.apache.catalina.tribes.membership.McastService" bind="127.0.0.1".../>
```

然后再在
```xml
<Engine name="Catalina" defaultHost="localhost" jvmRoute="jvm1"></Engine>
```
标签中添加jvmRoute属性，每个tomcat的jvmRoute不同，该属性将作为sessionId的后缀。

将这个tomcat复制到不同服务器中，如果是在同一服务器中，需要修改server.xml中的三个端口，以防冲突：

```xml

<Server port="8006" shutdown="SHUTDOWN"><!-- 服务端口 -->
    //...
    <Connector port="8081" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" /><!-- http端口 -->
    //...
    <Connector port="8010" protocol="AJP/1.3" redirectPort="8443" /><!-- AJP端口 --> 
   //...
</Server>

```

分别启动项目后，若用同一浏览器访问不同tomcat，会发现sessionId是一样的（后缀jvmRoute不同）这说明tomcat之间复制了session。

### 3. 负载均衡器Apache的配置

在apache conf的目录下新建一个balance.conf配置文件，内容如下：
   
```
#提供基础的代理功能
LoadModule proxy_module modules/mod_proxy.so
#提供负载均衡的功能
LoadModule proxy_balancer_module modules/mod_proxy_balancer.so
#代理http协议
LoadModule proxy_http_module modules/mod_proxy_http.so
#代理ajp协议
LoadModule proxy_ajp_module modules/mod_proxy_ajp.so
#支持转发websocket协议
LoadModule proxy_wstunnel_module modules/mod_proxy_wstunnel.so

#负载均衡的算法模块
LoadModule lbmethod_byrequests_module modules/mod_lbmethod_byrequests.so
LoadModule slotmem_shm_module modules/mod_slotmem_shm.so
#兼容低版本访问
LoadModule access_compat_module modules/mod_access_compat.so

ProxyRequests Off
#ProxyRequests是Off，负载均衡器就是一个反向代理，即：对于客户端而言它就像是原始服务器，并且客户端不需要进行任何特别的设置
#配置含义是凡是到根目录“/”的请求均交由均衡器“balancer://cluster/”处理，对应下面<Proxy balancer://cluster>

#websocket是长连接不能像http请求那样被负载器一个个地转发，这里只能直接转交到处理服务器上
#转发到具体的websocket服务器上，该规则需配置在“/”前面，否则会被先当作http处理
ProxyPass /building/systemInfo ws://192.168.100.1:8080/building/systemInfo
ProxyPass /building/chat/ ws://192.168.100.1:8080/building/chat/

ProxyPass / balancer://cluster/ stickysession=JSESSIONID


#设置代理的算法
#ProxySet lbmethod=bytraffic

#代理关联配置loadfactor可以分发请求权重，loadfactor越大，权重越大
#route与tomcat中server.xml<Engine>标签的jvmRoute属性一致
<Proxy balancer://cluster>
  #用http协议分发
  BalancerMember http://192.168.100.1:8080 loadfactor=1 route=jvm1
  BalancerMember http://192.168.100.130:8080 loadfactor=1 route=jvm2

  #用ajp协议分发
  #BalancerMember ajp://192.168.100.1:8009 loadfactor=1 route=jvm1
  #BalancerMember ajp://192.168.100.130:8009 loadfactor=1 route=jvm2

  #热部署，当着备份服务，当jvm1和jvm2宕机时，就自动访问jvm3
  #BalancerMember http://localhost:9080 loadfactor=1 route=jvm3  status=+H
</Proxy>

#负载均衡控制台，通过http://localhost/balancer-manager 访问
<Location /balancer-manager>
    SetHandler balancer-manager
    Order Deny,Allow
    Allow from all
    #Allow from localhost
</Location>
```

打开conf/httpd.conf将balance.conf引进去，在httpd.conf最下面通过下面命令引入

```sh
include conf\balance.conf
```

> 若想在控制台上查看监控情况，可以在地址栏上输入http://localhost/balancer-manager，但注意生产环境中要禁止。

配置中，可以选用两种连接方式：HTTP和AJP，此外“stickysession=JSESSIONID”保证了负载均衡机制能够感知会话，并总是将来自于同一会话的请求发送到相同服务器上。


## 六、关于单元测试
查看单元测试覆盖率可以在项目根目录下运行如下命令:mvn cobertura:cobertura
常用命令

查看cobertura插件的帮助
mvn cobertura:help

清空cobertura插件运行结果
mvn cobertura:clean

运行cobertura的检查任务     
mvn cobertura:check      

在target文件夹下出现了一个site目录，下面是一个静态站点，里面就是单元测试的覆盖率报告。

## 七、关于Spring Security內建表达式说明
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


## 八、关于Spring data内建表达式说明

|表达式				|						例子								|					jpql查询语句							|
|-------------------|-------------------------------------------------------|-------------------------------------------------------|
|And				|findByLastnameAndFirstname								|… where x.lastname = ?1 and x.firstname = ?2			|
|Or					|findByLastnameOrFirstname								|… where x.lastname = ?1 or x.firstname = ?2			|
|Is,Equals			|findByFirstname,findByFirstnameIs,findByFirstnameEqual	|… where x.firstname = 1?								|
|Between			|findByStartDateBetween									|… where x.startDate between ?1 and ?2					|
|LessThan			|findByAgeLessThan										|… where x.age < ?1										|
|LessThanEqual		|findByAgeLessThanEqual									|… where x.age <= ?1									|
|GreaterThan		|findByAgeGreaterThan									|… where x.age > ?1										|
|GreaterThanEqual	|findByAgeGreaterThanEqual								|… where x.age >= ?1									|
|After				|findByStartDateAfter									|… where x.startDate > ?1								|
|Before				|findByStartDateBefore									|… where x.startDate < ?1								|
|IsNull				|findByAgeIsNull										|… where x.age is null									|
|IsNotNull,NotNull	|findByAge(Is)NotNull									|… where x.age not null									|
|Like				|findByFirstnameLike									|… where x.firstname like ?1							|
|NotLike			|findByFirstnameNotLike									|… where x.firstname not like ?1						|
|StartingWith		|findByFirstnameStartingWith							|… where x.firstname like ?1 (parameter bound with appended %)|
|EndingWith			|findByFirstnameEndingWith								|… where x.firstname like ?1 (parameter bound with prepended %)|
|Containing			|findByFirstnameContaining								|… where x.firstname like ?1 (parameter bound wrapped in %)|
|OrderBy			|findByAgeOrderByLastnameDesc							|… where x.age = ?1 order by x.lastname desc			|
|Not				|findByLastnameNot										|… where x.lastname <> ?1								|
|In					|findByAgeIn(Collection ages)							|… where x.age in ?1									|
|NotIn				|findByAgeNotIn(Collection age)							|… where x.age not in ?1								|
|True				|findByActiveTrue()										|… where x.active = true								|
|False				|findByActiveFalse()									|… where x.active = false								|
|IgnoreCase			|findByFirstnameIgnoreCase								|… where UPPER(x.firstame) = UPPER(?1)					|


