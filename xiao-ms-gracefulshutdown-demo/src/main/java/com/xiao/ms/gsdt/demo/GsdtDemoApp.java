package com.xiao.ms.gsdt.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GsdtDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(GsdtDemoApp.class, args);
    }
}
