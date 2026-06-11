/**
 * 阿里云OSS对象存储配置
 */
package com.bms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS对象存储配置属性
 */
@Configuration
@ConfigurationProperties(prefix = "oss")
@Data
public class OssConfig {

    /**
     * 是否启用OSS存储
     */
    private boolean enabled = false;

    /**
     * 阿里云OSS区域节点
     * 例如：oss-cn-hangzhou
     */
    private String endpoint;

    /**
     * 访问密钥ID
     */
    private String accessKeyId;

    /**
     * 访问密钥密码
     */
    private String accessKeySecret;

    /**
     * OSS Bucket名称
     */
    private String bucketName;

    /**
     * 自定义域名（可选，用于CDN加速）
     */
    private String customDomain;

    /**
     * 文件存储的基础路径
     */
    private String basePath = "books/";

    /**
     * 图片URL前缀（如果启用了自定义域名则使用自定义域名）
     */
    public String getFileUrlPrefix() {
        if (customDomain != null && !customDomain.isEmpty()) {
            return customDomain.endsWith("/") ? customDomain : customDomain + "/";
        }
        return "https://" + bucketName + "." + endpoint + "/";
    }
}
