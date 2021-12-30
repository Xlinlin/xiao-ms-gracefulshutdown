package com.xiao.ms.gsdt.starter.tomcat;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xiao.ms.gsdt.starter.properties.GracefulShutdownProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * TomcatGracefulShutdownAutoConfiguration <br>
 *
 * @date: 2021/12/30 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
public class TomcatGracefulShutdownAutoConfiguration {

    @Bean
    public TomcatGracefulShutdown tomcatGracefulShutdown(
            ApplicationContext context, NacosDiscoveryProperties nacosDiscoveryProperties,
            GracefulShutdownProperties gracefulShutdownProperties) {
        return new TomcatGracefulShutdown(context, gracefulShutdownProperties, nacosDiscoveryProperties);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer(TomcatGracefulShutdown connector) {
        return factory -> factory.addConnectorCustomizers(connector);
    }

}
