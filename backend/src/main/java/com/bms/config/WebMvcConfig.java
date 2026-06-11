/**
 * Web MVC 配置
 * 配置静态资源访问
 */
package com.bms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.storage.local-path:uploads}")
    private String localStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地文件访问路径
        // 访问 /uploads/books/2024/01/xxx.jpg 会映射到 uploads/books/2024/01/xxx.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + localStoragePath + "/");
    }
}
