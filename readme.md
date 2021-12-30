# SpringCloud 微服务优雅停机

## 背景介绍

微服务更新时，我们希望等待新的服务启动后，切断旧的服务流量，并且等待旧的服务处理完已接收的请求后再进行关闭服务，从而保持数据不丢失

## 原理简介

1. 当服务器或K8S要关闭旧的服务是，会执行kill命令，发送一个SIGTERM信号给到JVM，通过JVM的ShutdownHook实现停止后的相关清理操作
2. 在Spring生态里面，可以通过捕捉ContextClosedEvent事件结合web容器(tomcat、undertow、jetty、netty、webflux等)来实现优雅停机
3. 本组件通过实现ApplicationListener<ContextClosedEvent>，接受收到close时间后，第一时间发起注册中心下线，然后等待一定时间后在进行关闭服务

## 环境信息

1. springboot 2.2.9
2. spring alibaba cloud 2.2.1
3. nacos 服务端 1.3.2

## 实现简述

1. 参考``com.xiao.ms.gsdt.starter.undertow.UndertowGracefulShutdown``实现
2. 当前仅实现tomcat和undertow的优雅停机

## 使用说明

1. pom文件引用

```xml

<dependency>
  <groupId>com.xiao.ms.graceful.sdt</groupId>
  <artifactId>xiao-ms-gracefulshutdown-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2. 相关配置，具体配置参考``com.xiao.ms.gsdt.starter.properties.GracefulShutdownProperties``<br>
   _其他nacos配置省略_
```yaml
server:
  graceful:
    shutdown:
      # 默认开启
      enabled: true
      # 优雅停机等待时间，单位MS，默认30S
      timeout: 30000
```

## 其他说明

1. Springboot 2.3.0 以后已经实现优雅停机，可以直接使用，详情查看官网资料
2. IDEA开发工具启动SpringBoot应用后，点击菜单栏的停止按钮就是kill命令
3. 在使用优雅停机后，logback的日志打印会立即停止，即发起kill命令后，logback不会在输出日志。可通过延迟关闭logback，具体配置如下：

```xml

<configuration>
  <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
  <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>

  <!-- 变量定义 -->
  <springProperty scope="context" name="appName" source="spring.application.name"/>
  <springProperty scope="context" name="ip" source="spring.cloud.client.ipAddress"/>
  <springProperty scope="context" name="active" source="spring.profiles.active"/>
  <springProperty scope="context" name="delay" source="server.graceful.shutdown.timeout"/>

  <!--为了防止进程退出时，内存中的数据丢失，请加上此选项，延迟logback关闭时间，打印关闭后更多的日志-->
  <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook">
    <!-- 可以保持跟graceful shutdown相关延迟配置配合 -->
    <delay>
      ${delay}
    </delay>
  </shutdownHook>

  <!-- 控制台日志 -->
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d %thread %highlight(%-5level)
        [${appName},%yellow(%X{X-B3-TraceId}),%green(%X{X-B3-SpanId}),%blue(%X{X-B3-ParentSpanId})]
        %logger{40} %message %exception{10} %n
      </pattern>
      <charset>utf8</charset>
    </encoder>
  </appender>

  <root level="info">
    <appender-ref ref="CONSOLE"/>
  </root>
</configuration>
```

## 其他演示操作
