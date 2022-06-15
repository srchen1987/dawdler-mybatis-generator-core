# dawdler-mybatis-generator-core

## 模块介绍

springcloud-mybatis-generator-core 是基于mybatis-generator-core进行二次开发实现的一套快速构建微服务的代码生成工具, 可以自动生成微服务的接口和实现类, 并且可以自动生成微服务的服务提供者和服务消费者以及前端的api和验证器.
### 1. 生成文件说明
 
| 文件 | 所属模块 | 描述 |
| :-: | :-: | :-: |
| entity | api | 数据库映射实体对象 |
| mapper | service | mybatis中mapper接口 |
| mapper.xml | service | mybatis中mapper的xml文件 |
| service | api | service层接口 |
| serviceImpl | service | service具体实现类 |
| controller | load-web | webApi接口(被web层远程加载使用) |
| controllerValidator | web | 校验文件(校验框架,请求webApi时会做相对的校验) |


### 2. 使用方式

#### 2.1 创建generatorConfig.xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>

	<classPathEntry
		location="/home/srchen/mysql-connector-java-8.0.12.jar" />
	<context id="dawdler-generator" targetRuntime="MyBatis3">
		<plugin type="org.mybatis.generator.plugins.SerializablePlugin" />
		<plugin type="org.mybatis.generator.plugins.AnywidePlugin">
			<property name="apiProject" value="demo-load-api" />
			<property name="serviceProject" value="demo-service" />
			<property name="loadWebProject" value="demo-load-web" />
			<property name="webProject" value="demo-api" />
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
			<property name="company" value="dawlder " />
		</commentGenerator>
		<jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
			connectionURL="jdbc:mysql://127.0.0.1:3306/sinopec?nullCatalogMeansCurrent=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false&amp;allowPublicKeyRetrieval=True"
			userId="platform" password="12345678">
		</jdbcConnection>
		<javaTypeResolver><!-- 映射java中的bigdecimal -->
			<property name="forceBigDecimals" value="false" />
		</javaTypeResolver>

		<javaModelGenerator
			targetPackage="com.anywide.demo.entity" targetProject="demo-load-api">
			<property name="trimStrings" value="true" />
		</javaModelGenerator>

		<sqlMapGenerator
			targetPackage="com.anywide.demo.dao.xml" targetProject="demo-service">
		</sqlMapGenerator>

		<javaClientGenerator type="XMLMAPPER"
			targetPackage="com.anywide.demo.dao" targetProject="demo-service">
		</javaClientGenerator>
		<table schema="" tableName="demo_user" domainObjectName="User"
			enableCountByExample="false" enableUpdateByExample="false"
			enableDeleteByExample="false" enableSelectByExample="false"
			selectByExampleQueryId="false"></table>
	</context>

</generatorConfiguration>
```

  以上配置文件中的说明如下: 

  | 配置名 | 描述 |
  | :-: | :-: |
  | apiProject | api项目(java项目名,或绝对路径) |
  | serviceProject | service项目(java项目名,或绝对路径) |
  | loadWebProject | loadWeb项目(java项目名,或绝对路径) |
  | webProject | web项目(java项目名,或绝对路径) |
  | chanelGroupId | 服务模块名用于生成@@FeignClient("服务模块名") |
  | targetPackageController | controller包名 |
  | targetPackageService | service接口包名 |
  | targetPackageServiceImpl | service实现层包名 |

  其他配置参考官网即可(或参考例子做调整)


#### 2.2 通过jar的生成方式


1.  clone本项目并安装.
   
   ```shell
   git clone https://github.com/srchen1987/springcloud-mybatis-generator-core.git

   mvn install #获取mybatis-generator-core-1.4.1.jar
   ```

2. 执行jar:

```shell

	java -jar mybatis-generator-core-1.4.1.jar -configfile generatorConfig.xml #支持绝对路径

```

注意：generatorConfig.xml 中的apiProject、serviceProject、loadWebProject、webProject需要填写项目中java source所在的绝对路径.

#### 2.3 eclipse插件方式

1. [下载eclipse](https://www.eclipse.org/downloads/)

2. 安装mybatis generator插件 
   
   点击 windows -> eclipse marketPlace 键入 mybatis generator 进行安装(目前版本是1.4.1).

3. clone本项目并安装.
   
  ```shell
   git clone https://github.com/srchen1987/springcloud-mybatis-generator-core.git

   mvn install #获取mybatis-generator-core-1.4.1.jar
   ```
   
4. 替换jar包

   找到对应的插件jar包,将下载的进行替换,笔者的jar在 /home/srchen/.p2/pool/plugins/org.mybatis.generator.core_1.4.1.202203082207.jar.
   
   插件存放位置在{用户目录}/.p2/pool/plugins/xxx.jar  （win mac linux发行版全部如此)

5. 重启eclipse  

6. eclipse运行插件
   
   在eclipse中打开项目,选择项目->配置->mybatis generator->选择generatorConfig.xml文件.
   
   注意：generatorConfig.xml 中的apiProject、serviceProject、loadWebProject、webProject需要填写项目名即可.

### 3. 基于源码二次开发

所有更改过的类中都注释了 jackson.song ,需要更改注释或其他需求的可以自行更改,更改完之后mvn install 可以获取到jar.

