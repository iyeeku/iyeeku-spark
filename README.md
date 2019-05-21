# iyeeku

项目说明
    
        该项目编写目的：用于个人工作之余实战学习互联网开发一些常用技术：搜索、缓存、微服务等,解决方案,系统设计,知识储备积累等。

演示地址 

## 特性

-   基于Spring + SpringMVC + MyBatis 开发 ，Maven管理项目，JDK1.8，数据库：ORACLE，开发工具 [Intellij idea](https://www.jetbrains.com/idea/)。
-   分布式缓存框架 [Ehcache](http://www.ehcache.org/)和 [Redis](https://redis.io/) ，分布式服务框架使用 [Dubbo](http://dubbo.apache.org/)。
-   消息队列服务 [ActiveMQ](http://activemq.apache.org/) , 搜索 [ElasticSearch](http://www.elastic.co/products/elasticsearch)
-   前端框架 [jQuery MiniUI](http://www.miniui.com/demo/#src=datagrid/celledit.html)
-   服务器 [Apache](https://www.apache.org/) + [WebLogic](https://www.oracle.com/middleware/weblogic/index.html) 集群
-   安全权限控制框架 [Spring-Security](http://spring.io/projects/spring-security)
-   [CAS](https://www.apereo.org/projects/cas)单点登录
-   WebSocket，RESTful 接口供移动端接入

## 更新日志

### 1.1.0

`2018-04-29`

-   基础环境搭建

## 开发构建

### 目录结构

```bash
├── /dist/                  # 项目打包输出目录 (github过滤)
├── /build/                 # 项目编译过程文件输出目录 (github过滤)
├── /iyeeku-core/           # 项目搭建核心模块代码包
│ ├── /src/                 # 代码夹
│ │ ├── /java               # 源代码
│ │ └── /resources          # 资源文件
├── /iyeeku-ext/            # 项目基础功能模块代码包（用户，角色，菜单，系统配置等基础功能）
│ ├── /src/                 # 代码夹
│ │ ├── /java               # 源代码
│ │ └── /resources          # 资源文件
├── /iyeeku-monitor/        # 提供APP客户端远程监控服务器，个人电脑PC相关功能，以及系统相关监控信息
│ ├── /src/                 # 代码夹
│ │ ├── /java               # 源代码
│ │ └── /resources          # 资源文件
├── /iyeeku-monitor-server  # 远程监控服务端java项目
│ ├── /src/                 # 代码夹
│ │ ├── /java               # 源代码
│ │ └── /resources          # 资源文件
├── /iyeeku-webapp/         # web功能模块代码包
│ ├── /src/                 # 代码夹
│ │ ├── /java               # 源代码
│ │ ├── /resources          # 资源文件
│ │ ├── /webapp             # web根目录
├── .build.xml              # Ant打包build.xml文件
├── .init.sql               # 数据脚本
├── .README.md              # README.md
└── .pom.xml                # maven依赖配置文件
```

文件命名说明

