package com.xiao.ms.gsdt.starter;

import com.xiao.ms.gsdt.starter.tomcat.TomcatGracefulShutdownAutoConfiguration;
import com.xiao.ms.gsdt.starter.undertow.UndertowGracefulShutdownAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 优雅关机 <br>
 *
 * @date: 2021/12/23 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@Configuration
@ComponentScan(basePackages = "com.xiao.ms.gsdt.starter.properties")
@ConditionalOnWebApplication
public class GracefulShutdownAutoConfiguration {

    /**
     * undertow优雅关闭
     */
    @Configuration
    @ConditionalOnClass(name = "io.undertow.server.handlers.GracefulShutdownHandler")
    @Import(UndertowGracefulShutdownAutoConfiguration.class)
    static class UndertowGracefulShutdown {

    }

    /**
     * tomcat优雅关闭
     */
    @Configuration
    @ConditionalOnClass(name = "org.apache.catalina.connector.Connector")
    @Import(TomcatGracefulShutdownAutoConfiguration.class)
    static class TomcatGracefulShutdown {

    }


}
