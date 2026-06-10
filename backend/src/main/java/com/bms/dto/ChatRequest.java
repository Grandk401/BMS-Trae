package com.bms.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String sessionId;  // 会话ID，为空则创建新会话
}
