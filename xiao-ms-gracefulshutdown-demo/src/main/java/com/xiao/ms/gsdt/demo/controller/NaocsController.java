package com.xiao.ms.gsdt.demo.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * nacos操作 <br>
 *
 * @date: 2021/12/22 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/api/nacos")
@Slf4j
public class NaocsController {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping("/info")
    public String getServerInfo() {
        return nacosDiscoveryProperties.getService() + ':' + nacosDiscoveryProperties.getIp() + ':'
                + nacosDiscoveryProperties.getPort();

    }

    @GetMapping("/sleep")
    public String sleep() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            log.info("Logback当期请求：" + i);
            System.out.println("当期请求：" + i);
            Thread.sleep(1000L);
        }
        return "请求成功!";
    }

    /**
     * 主动发起下线
     *
     * @return java.lang.String
     * @author llxiao
     * @date 2021/12/22
     **/
    @GetMapping("/deregister")
    public String deregisterInstance() {
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();
        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();
        log.info("服务发起主动注销流程, serviceName:{}, groupName:{}, clusterName:{}, ip:{}, port:{}", serviceName, groupName,
                clusterName, ip, port);
        String serInfo = nacosDiscoveryProperties.getService() + ':' + nacosDiscoveryProperties.getIp() + ':'
                + nacosDiscoveryProperties.getPort();
        try {
            nacosDiscoveryProperties.namingServiceInstance().deregisterInstance(serviceName, groupName, ip,
                    port, clusterName);
            threadPoolTaskExecutor.execute(() -> {
                try {

                    log.info("服务：{} 线程休眠40S。。。。。。", serInfo);
                    Thread.sleep(40000);
                    log.info("线程执行40S结束。。。。。。");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threadPoolTaskExecutor.execute(() -> {
                try {
                    log.info("服务：{} 线程休眠5S。。。。。。", serInfo);
                    Thread.sleep(5000);
                    log.info("线程执行5S结束。。。。。。");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threadPoolTaskExecutor.execute(() -> {
                try {
                    log.info("服务：{} 线程休眠10S。。。。。。", serInfo);
                    Thread.sleep(5000);
                    log.info("线程执行10S结束。。。。。。");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (NacosException e) {
            log.error("服务发起主动注销失败，异常信息：", e);
            return "error";
        }
        return "success";
    }

    /**
     * 主动发起上线
     *
     * @return java.lang.String
     * @author llxiao
     * @date 2021/12/22
     **/
    @GetMapping("/register")
    public String registerInstance() {
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();
        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();
        log.info("服务发起主动注册流程, serviceName:{}, groupName:{}, clusterName:{}, ip:{}, port:{}", serviceName, groupName,
                clusterName, ip, port);
        try {
            nacosDiscoveryProperties.namingServiceInstance().registerInstance(serviceName, groupName, ip,
                    port, clusterName);
        } catch (NacosException e) {
            log.error("服务发起主动注销失败，异常信息：", e);
            return "error";
        }
        return "success";
    }
}
