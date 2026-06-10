package com.bms.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 聊天记录实体
 */
@Data
public class AiChatRecord {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 会话ID（用于关联多轮对话）
     */
    private String sessionId;

    /**
     * 用户消息
     */
    private String message;

    /**
     * AI 回复
     */
    private String response;

    /**
     * 推荐的书籍ID列表（JSON格式）
     */
    private String bookRecommendations;

    /**
     * 本次对话使用的 Token 数量
     */
    private Integer tokenUsed;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
