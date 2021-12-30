package com.xiao.ms.gsdt.starter.undertow;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xiao.ms.gsdt.starter.properties.GracefulShutdownProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * UndertowGracefulShutdownAutoConfiguration <br>
 *
 * @date: 2021/12/30 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
public class UndertowGracefulShutdownAutoConfiguration {

    @Bean
    public UndertowGracefulShutdownWrapper undertowGracefulShutdownWrapper() {
        return new UndertowGracefulShutdownWrapper();
    }

    @Bean
    public UndertowGracefulShutdown undertowGracefulShutdown(
            UndertowGracefulShutdownWrapper undertowGracefulShutdownWrapper,
            ApplicationContext context, NacosDiscoveryProperties nacosDiscoveryProperties,
            GracefulShutdownProperties gracefulShutdownProperties) {
        return new UndertowGracefulShutdown(undertowGracefulShutdownWrapper, context, gracefulShutdownProperties,
                nacosDiscoveryProperties);
    }

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer(UndertowGracefulShutdownWrapper handler) {
        return factory -> factory.addDeploymentInfoCustomizers(info -> info.addInitialHandlerChainWrapper(handler));
    }
}
