package com.xiao.ms.gsdt.starter.tomcat;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xiao.ms.gsdt.starter.GracefulShutdown;
import com.xiao.ms.gsdt.starter.properties.GracefulShutdownProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationContext;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TomcatGracefulShutdown <br>
 *
 * @date: 2021/12/30 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@Slf4j
public class TomcatGracefulShutdown extends GracefulShutdown implements TomcatConnectorCustomizer {

    private volatile Connector connector;

    public TomcatGracefulShutdown(
            ApplicationContext context, GracefulShutdownProperties gracefulShutdownProperties,
            NacosDiscoveryProperties nacosDiscoveryProperties) {
        super(context, gracefulShutdownProperties, nacosDiscoveryProperties);
        log.info("初始化TomcatGracefulShutdown....");
    }

    /**
     * 具体的容器实现shutdown
     *
     * @author llxiao
     * @date 2021/12/30
     **/
    @Override
    public void webShutdown() {
        System.out.println(new Date() + " Tomcat 开始shutdown。。。。。");
        connector.pause();
        Executor executor = connector.getProtocolHandler().getExecutor();
        if (executor instanceof ExecutorService) {
            ExecutorService executorService = (ExecutorService) executor;
            executorService.shutdown();
            Duration duration = Duration.ofMillis(gracefulShutdownProperties.getTimeout());
            if (timeoutReached(duration, executorService)) {
                log.warn(
                        "Tomcat did not terminate gracefully within " + duration
                                .getSeconds() + " seconds");
                executorService.shutdownNow();
                if (timeoutReached(duration, executorService)) {
                    log.warn("Timeout reached (pending requests aborted)");
                }
            }
        }
        System.out.println(new Date() + "所有服务执行完毕，程序直接退出。。。。。");
    }

    /**
     * tomcat容器超时等待
     *
     * @param timeout         :
     * @param executorService :
     * @return boolean
     * @author llxiao
     * @date 2021/12/30
     **/
    private boolean timeoutReached(Duration timeout, ExecutorService executorService) {
        boolean flag = false;
        try {
            flag = executorService.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println("Tomcat 容器挂起时被强行中断");
        }
        return flag;
    }

    @Override
    public void customize(org.apache.catalina.connector.Connector connector) {
        this.connector = connector;
    }
}
