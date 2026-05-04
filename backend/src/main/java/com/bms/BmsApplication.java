/**
 * 图书管理系统后端应用启动类
 * <p>
 * 这是 Spring Boot 应用的入口类，负责启动整个后端服务。
 * 系统提供图书管理、用户认证、借阅记录管理等功能。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 应用主类
 */
@SpringBootApplication
@MapperScan("com.bms.mapper")
@Slf4j
@EnableScheduling
@EnableAsync
public class BmsApplication {

    /**
     * 应用入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        log.info("启动图书管理系统后端服务...");
        SpringApplication.run(BmsApplication.class, args);
        log.info("图书管理系统后端服务启动成功！");
    }
}
