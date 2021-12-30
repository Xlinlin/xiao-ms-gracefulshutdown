package com.xiao.ms.gsdt.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * TODO  GracefulShutdownProperties <br>
 *
 * @date: 2021/12/27 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@Configuration
@ConfigurationProperties(prefix = "server.graceful.shutdown")
@Data
public class GracefulShutdownProperties {

    /**
     * 是否开启，默认开启
     */
    private boolean enabled = true;

    /**
     * 等待超时时间，单位：ms，默认等待 30S，如果设置waitConnection，此参数无效
     */
    private long timeout = 30000L;

    /**
     * 是否等待连接处理完
     */
    private boolean waitConnection = false;
}