package com.xiao.ms.gsdt.starter.undertow;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xiao.ms.gsdt.starter.GracefulShutdown;
import com.xiao.ms.gsdt.starter.properties.GracefulShutdownProperties;
import io.undertow.server.handlers.GracefulShutdownHandler;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * UndertowGracefulShutdown 优雅停机 <br>
 *
 * @date: 2021/12/23 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@Slf4j
public class UndertowGracefulShutdown extends GracefulShutdown {

    private final UndertowGracefulShutdownWrapper undertowGracefulShutdownWrapper;

    public UndertowGracefulShutdown(UndertowGracefulShutdownWrapper undertowGracefulShutdownWrapper,
                                    ApplicationContext context, GracefulShutdownProperties gracefulShutdownProperties,
                                    NacosDiscoveryProperties nacosDiscoveryProperties) {
        super(context, gracefulShutdownProperties, nacosDiscoveryProperties);
        this.undertowGracefulShutdownWrapper = undertowGracefulShutdownWrapper;
        log.info("初始化UndertowGracefulShutdown....");
    }

    /**
     * 具体的容器实现shutdown
     *
     * @author llxiao
     * @date 2021/12/30
     **/
    @Override
    public void webShutdown() {
        GracefulShutdownHandler gracefulShutdownHandler = undertowGracefulShutdownWrapper.getGracefulShutdownHandler();
        System.out.println(new Date() + " Undertow shutdown");
        gracefulShutdownHandler.shutdown();
        try {
            if (!gracefulShutdownHandler.awaitShutdown(gracefulShutdownProperties.getTimeout())) {
                System.out.println(new Date() + " 有活跃请求，等待执行。。。。。");
            } else {
                System.out.println(new Date() + " 空闲时间，强制等待时间：" + gracefulShutdownProperties.getTimeout() + " ms");
                try {
                    Thread.sleep(gracefulShutdownProperties.getTimeout());
                } catch (InterruptedException e) {
                    System.out.println("优化停机后，产生了线程中断.............");
                }
            }
        } catch (Exception e) {
            System.out.println("优雅关闭异常");
            e.printStackTrace();
        }
        System.out.println(new Date() + " 所有服务执行完毕，程序直接退出。。。。。");
    }
}
