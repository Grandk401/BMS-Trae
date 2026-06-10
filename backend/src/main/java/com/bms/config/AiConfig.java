package com.bms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    /**
     * AI 服务提供商: qwen(通义千问) | ernie(文心一言) | chatglm(智谱)
     */
    private String provider = "qwen";

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API 基础地址（国内AI通常不需要）
     */
    private String baseUrl;

    /**
     * 模型名称
     * 通义千问: qwen-turbo, qwen-plus, qwen-max
     * 文心一言: ernie-bot, ernie-bot-4
     * ChatGLM: glm-4, glm-4-flash
     */
    private String model = "qwen-turbo";

    /**
     * 每日对话次数限制
     */
    private int dailyLimit = 10;
}
