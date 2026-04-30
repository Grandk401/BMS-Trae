/**
 * 数据初始化组件
 */
package com.bms.config;

import com.bms.entity.User;
import com.bms.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void run(String... args) {
        log.info("开始初始化数据...");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(DEFAULT_PASSWORD);

        if (userMapper.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encodedPassword);
            admin.setRole("ADMIN");
            userMapper.insert(admin);
            log.info("创建系统管理员用户: admin");
        } else {
            log.info("系统管理员用户已存在: admin");
        }

        if (userMapper.findByUsername("librarian") == null) {
            User librarian = new User();
            librarian.setUsername("librarian");
            librarian.setPassword(encodedPassword);
            librarian.setRole("LIBRARIAN");
            userMapper.insert(librarian);
            log.info("创建图书管理员用户: librarian");
        } else {
            log.info("图书管理员用户已存在: librarian");
        }

        if (userMapper.findByUsername("reader") == null) {
            User reader = new User();
            reader.setUsername("reader");
            reader.setPassword(encodedPassword);
            reader.setRole("READER");
            userMapper.insert(reader);
            log.info("创建普通读者用户: reader");
        } else {
            log.info("普通读者用户已存在: reader");
        }

        log.info("数据初始化完成");
    }
}
