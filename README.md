# dawdler-mybatis-generator-core

## 模块介绍

dawdler-mybatis-generator-core 是基于mybatis-generator-core进行二次开发实现的一套快速构建微服务的代码生成工具, 可以自动生成微服务的接口和实现类, 并且可以自动生成微服务的服务提供者和服务消费者以及前端的api和验证器.

### 1. 生成文件说明

|        文件         | 所属模块 |                      描述                      |
| :-----------------: | :------: | :--------------------------------------------: |
|       entity        |   api    |               数据库映射实体对象               |
|       mapper        | service  |              mybatis中mapper接口               |
|     mapper.xml      | service  |            mybatis中mapper的xml文件            |
|       service       |   api    |                 service层接口                  |
|     serviceImpl     | service  |               service具体实现类                |
|     controller      | load-web |        webApi接口(被web层远程加载使用)         |
| controllerValidator |   web    | 校验文件(校验框架, 请求webApi时会做相对的校验) |

### 2. 使用方式

#### 2.1 创建generatorConfig.xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>

 <classPathEntry
  location="/home/srchen/.m2/repository/com/mysql/mysql-connector-j/8.0.32/mysql-connector-j-8.0.32.jar" />
 <context id="mydemo" targetRuntime="MyBatis3">
 <property name="apiProject" value="/home/srchen/dawdler-workspace/my-demo/src/main/java"/>
 <property name="serviceProject" value="/home/srchen/dawdler-workspace/my-demo/src/main/java"/>
 <property name="webProject" value="/home/srchen/dawdler-workspace/my-demo/src/main/java"/>
 <property name="serviceType" value="remote"/>
  <plugin type="org.mybatis.generator.plugins.SerializablePlugin" />
  <plugin type="org.mybatis.generator.plugins.AnywidePlugin">
   <property name="chanelGroupId" value="user" />
   <property name="targetPackageController"
    value="com.anywide.demo.controller" />
   <property name="targetPackageService"
    value="com.anywide.demo.service" />
   <property name="targetPackageServiceImpl"
    value="com.anywide.demo.service.impl" />
  </plugin>
  <commentGenerator
   type="org.mybatis.generator.internal.AnywideCommentGenerator">
   <property name="suppressAllComments" value="false" />
   <property name="company" value="dawlder" />
  </commentGenerator>
  <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
   connectionURL="jdbc:mysql://127.0.0.1:3306/my_order?nullCatalogMeansCurrent=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false&amp;allowPublicKeyRetrieval=True"
   userId="root" password="">
  </jdbcConnection>
  <javaTypeResolver><!-- 映射java中的bigdecimal -->
   <property name="forceBigDecimals" value="false" />
  </javaTypeResolver>

  <javaModelGenerator
   targetPackage="com.anywide.demo.entity" targetProject="none">
   <property name="trimStrings" value="true" />
  </javaModelGenerator>

  <sqlMapGenerator
   targetPackage="com.anywide.demo.mapper.xml" targetProject="none">
  </sqlMapGenerator>

  <javaClientGenerator type="XMLMAPPER"
   targetPackage="com.anywide.demo.mapper" targetProject="/home/srchen/dawdler-workspace/my-demo/src/main/resources">
  </javaClientGenerator>
  <table schema="" tableName="tb_order" domainObjectName="Order"
   enableCountByExample="false" enableUpdateByExample="false"
   enableDeleteByExample="false" enableSelectByExample="false"
   selectByExampleQueryId="false"></table>
 </context>
</generatorConfiguration>
```

  以上配置文件中的说明如下:

  |          配置名          |                    描述                    |
  | :----------------------: | :----------------------------------------: |
  |        apiProject        |             api项目(绝对路径)              |
  |      serviceProject      |           service项目(绝对路径)            |
  |        webProject        |             web项目(绝对路径)              |
  |       serviceType        | remote或local 用于区分是远程服务与本地服务 |
  |      chanelGroupId       |  服务模块名用于生成@Service("服务模块名")  |
  | targetPackageController  |               controller包名               |
  |   targetPackageService   |              service接口包名               |
  | targetPackageServiceImpl |             service实现层包名              |

 其他配置参考官网即可(或参考例子做调整)

 targetProject="none" 如果填写none javaModelGenerator等同于apiProject中的路径、sqlMapGenerator与javaClientGenerator等同于serviceProject中的路径.

#### 2.2 通过jar的生成方式

##### 2.2.1 clone本项目并安装

```shell
   git clone https://github.com/srchen1987/dawdler-mybatis-generator-core.git

   mvn install #获取mybatis-generator-core-1.4.1.jar
   ```

##### 2.2.2 执行jar

```shell
 java -jar mybatis-generator-core-1.4.1.jar -configfile generatorConfig.xml #支持绝对路径
```

注意：generatorConfig.xml 中的apiProject、serviceProject、webProject需要填写项目中java source所在的绝对路径.

### 3. 基于源码二次开发

所有更改过的类中都注释了 jackson.song , 需要更改注释或其他需求的可以自行更改, 更改完之后mvn install 可以获取到jar.
