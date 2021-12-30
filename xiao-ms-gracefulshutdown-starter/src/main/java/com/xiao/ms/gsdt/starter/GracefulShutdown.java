package com.xiao.ms.gsdt.starter;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import java.util.Date;

import com.xiao.ms.gsdt.starter.properties.GracefulShutdownProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 通用shutdown <br>
 *
 * @date: 2021/12/30 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
public abstract class GracefulShutdown implements ApplicationListener<ContextClosedEvent> {

    protected final ApplicationContext context;

    protected final NacosDiscoveryProperties nacosDiscoveryProperties;

    protected final GracefulShutdownProperties gracefulShutdownProperties;

    public GracefulShutdown(ApplicationContext context, GracefulShutdownProperties gracefulShutdownProperties,
                            NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.context = context;
        this.gracefulShutdownProperties = gracefulShutdownProperties;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println(new Date() + " 服务即将关闭......");
        if (gracefulShutdownProperties.isEnabled()) {
            deregister();
            webShutdown();
        }
        // 其他动作，比如MQ停止等
    }

    /**
     * 具体的容器实现shutdown
     *
     * @author llxiao
     * @date 2021/12/30
     **/
    public abstract void webShutdown();


    /**
     * naocse 反注册
     */
    private void deregister() {
        System.out.println(new Date() + " 发起注册中心立即下线.....");
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();
        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();
        System.out.println(
                new Date() + " 服务发起主动注销流程, serviceName:" + serviceName + ", groupName:" + groupName + ", clusterName:" + clusterName
                        + ", ip: "
                        + ip + ", port:" + port);
        try {
            nacosDiscoveryProperties.namingServiceInstance().deregisterInstance(serviceName, groupName, ip,
                    port, clusterName);
        } catch (NacosException e) {
            System.out.println("服务发起主动注销失败，异常信息：");
            e.printStackTrace();
        }

    }
}
