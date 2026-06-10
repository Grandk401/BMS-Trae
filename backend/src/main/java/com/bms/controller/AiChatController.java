package com.bms.controller;

import com.bms.common.Result;
import com.bms.dto.ChatRequest;
import com.bms.dto.ChatResponse;
import com.bms.service.AiChatService;
import com.bms.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * AI 聊天接口
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String username = UserContext.getUsername();
            ChatResponse response = aiChatService.chat(username, request);
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取今日剩余次数
     */
    @GetMapping("/remaining")
    public Result<Map<String, Object>> getRemainingCount() {
        try {
            String username = UserContext.getUsername();
            int remaining = aiChatService.getRemainingCount(username);
            Map<String, Object> result = new HashMap<>();
            result.put("remaining", remaining);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取会话历史记录（需要验证用户权限）
     */
    @GetMapping("/history/{sessionId}")
    public Result<List<Map<String, Object>>> getHistory(@PathVariable String sessionId) {
        try {
            String username = UserContext.getUsername();
            // 获取当前用户ID用于验证权限
            List<Map<String, Object>> history = aiChatService.getHistory(sessionId, UserContext.getUserId());
            return Result.success(history);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
