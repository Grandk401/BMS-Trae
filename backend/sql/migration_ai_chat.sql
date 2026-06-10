-- =============================================
-- AI 聊天功能数据库迁移脚本
-- 执行方式: mysql -u root -p bms < backend/sql/migration_ai_chat.sql
-- =============================================

-- 1. 给 user 表添加 AI 聊天相关字段
ALTER TABLE user
ADD COLUMN IF NOT EXISTS ai_chat_count INT DEFAULT 0 COMMENT '今日 AI 聊天次数',
ADD COLUMN IF NOT EXISTS ai_chat_date DATE COMMENT 'AI 聊天计数日期';

-- 添加索引（可选，提高查询效率）
ALTER TABLE user ADD INDEX idx_ai_chat_date (ai_chat_date);

-- =============================================
-- 2. 创建 AI 聊天记录表
-- =============================================
DROP TABLE IF EXISTS ai_chat_record;

CREATE TABLE ai_chat_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id INT NOT NULL COMMENT '用户ID',
    user_role VARCHAR(20) COMMENT '用户角色（冗余存储）',
    session_id VARCHAR(64) COMMENT '会话ID（用于关联多轮对话）',
    message TEXT NOT NULL COMMENT '用户消息',
    response TEXT COMMENT 'AI 回复',
    book_recommendations JSON COMMENT '推荐的书籍ID列表（JSON格式）',
    token_used INT DEFAULT 0 COMMENT '本次对话使用的 Token 数量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at),
    CONSTRAINT fk_ai_chat_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天记录表';

-- =============================================
-- 说明：
-- 1. session_id 用于关联同一会话中的多轮对话
-- 2. book_recommendations 存储 AI 推荐的书 ID，便于后续统计
-- 3. token_used 用于统计 Token 消耗（可选功能）
-- =============================================
