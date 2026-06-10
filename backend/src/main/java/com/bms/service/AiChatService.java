package com.bms.service;

import com.bms.config.AiConfig;
import com.bms.dto.ChatRequest;
import com.bms.dto.ChatResponse;
import com.bms.entity.AiChatRecord;
import com.bms.entity.Book;
import com.bms.entity.User;
import com.bms.mapper.AiChatRecordMapper;
import com.bms.mapper.BookMapper;
import com.bms.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AiChatRecordMapper aiChatRecordMapper;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 上下文最大字符数（保守估计，大约 3000 tokens）
    private static final int MAX_CONTEXT_CHARS = 12000;

    // 最大历史记录数（防止查询过多）
    private static final int MAX_HISTORY_RECORDS = 20;

    public ChatResponse chat(String username, ChatRequest request) {
        User user = userMapper.findByUsername(username);

        // 1. 检查限流
        checkRateLimit(user);

        // 2. 获取或创建会话ID
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
        }

        // 3. 加载历史上下文
        List<AiChatRecord> history = loadHistory(sessionId);

        // 4. 查询在馆图书
        List<Book> availableBooks = bookMapper.findAvailableBooks();

        // 5. 构建系统提示词（包含真实图书列表）
        String systemPrompt = buildSystemPrompt(availableBooks);

        // 6. 调用 AI
        String aiReply = callAi(request.getMessage(), history, systemPrompt);

        // 7. 提取书籍推荐（根据 AI 回复中的书名去数据库匹配）
        List<ChatResponse.BookRecommendDTO> bookRecommendations = extractBookRecommendationsFromReply(aiReply, availableBooks);

        // 8. 清理回复中的标记
        aiReply = cleanResponse(aiReply);

        // 9. 保存聊天记录
        saveChatRecord(user, sessionId, request.getMessage(), aiReply, bookRecommendations);

        // 10. 返回响应（包含 sessionId）
        ChatResponse response = new ChatResponse(aiReply, bookRecommendations);
        response.setSessionId(sessionId);
        return response;
    }

    /**
     * 构建系统提示词，包含真实图书列表
     */
    private String buildSystemPrompt(List<Book> availableBooks) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个智能图书助手，专门帮助用户推荐图书和解答图书相关问题。\n\n");

        sb.append("【重要】你只能推荐以下在馆图书列表中的书籍，禁止推荐不在列表中的书籍！\n\n");

        // 添加在馆图书列表
        sb.append("=== 在馆图书列表 ===\n");
        for (Book book : availableBooks) {
            sb.append(String.format("- 书名：《%s》，作者：%s，分类：%s\n",
                    book.getTitle(),
                    book.getAuthor() != null ? book.getAuthor() : "未知",
                    book.getCategory() != null ? book.getCategory() : "未分类"));
        }
        sb.append("=== 列表结束 ===\n\n");

        sb.append("你可以：\n");
        sb.append("1. 根据用户的兴趣、阅读偏好从上述列表中推荐2-3本合适的书籍\n");
        sb.append("2. 介绍图书内容、作者等信息\n");
        sb.append("3. 告诉用户如何借阅图书\n");
        sb.append("4. 回答关于图书馆的问题\n\n");

        sb.append("如果用户询问与图书、阅读、图书馆无关的问题，请礼貌地引导用户回到图书话题。\n\n");

        sb.append("【推荐格式】当你推荐书籍时，请在回复末尾加上推荐标记，格式为：\n");
        sb.append("【书籍推荐】书名1,书名2,书名3\n");
        sb.append("注意：只推荐列表中存在的书籍，书名要完全一致！\n");

        return sb.toString();
    }

    private void checkRateLimit(User user) {
        LocalDate today = LocalDate.now();

        if (user.getAiChatDate() == null || !user.getAiChatDate().equals(today)) {
            // 重置计数
            user.setAiChatCount(0);
            user.setAiChatDate(today);
        }

        if (user.getAiChatCount() >= aiConfig.getDailyLimit()) {
            throw new RuntimeException("今日 AI 聊天次数已用完（每日限制 " + aiConfig.getDailyLimit() + " 次）");
        }

        // 增加计数
        user.setAiChatCount(user.getAiChatCount() + 1);
        userMapper.updateAiChatCount(user.getId(), user.getAiChatCount(), today);
    }

    /**
     * 加载历史记录，并进行截断处理
     */
    private List<AiChatRecord> loadHistory(String sessionId) {
        // 先获取所有历史记录
        List<AiChatRecord> allHistory = aiChatRecordMapper.findBySessionId(sessionId);

        if (allHistory.isEmpty()) {
            return Collections.emptyList();
        }

        // 如果历史记录超过最大数，截断最旧的部分
        if (allHistory.size() > MAX_HISTORY_RECORDS) {
            // 只保留最近 MAX_HISTORY_RECORDS 条
            allHistory = allHistory.subList(
                    allHistory.size() - MAX_HISTORY_RECORDS,
                    allHistory.size()
            );
        }

        // 如果总字符数超过限制，渐进式截断
        return truncateHistoryIfNeeded(allHistory);
    }

    /**
     * 如果上下文过长，截断最久远的历史记录
     */
    private List<AiChatRecord> truncateHistoryIfNeeded(List<AiChatRecord> history) {
        if (history.isEmpty()) {
            return history;
        }

        // 计算当前上下文的总字符数
        int totalChars = calculateContextChars(history);

        // 如果超过限制，逐步截断最旧的消息
        while (totalChars > MAX_CONTEXT_CHARS && history.size() > 1) {
            // 移除最旧的一条记录
            history = history.subList(1, history.size());
            totalChars = calculateContextChars(history);
        }

        return history;
    }

    /**
     * 计算上下文的总字符数（粗略估算 token）
     */
    private int calculateContextChars(List<AiChatRecord> history) {
        int chars = 0;  // 不再重复计算 SYSTEM_PROMPT，因为每次都是新鲜的
        for (AiChatRecord record : history) {
            chars += record.getMessage() != null ? record.getMessage().length() : 0;
            chars += record.getResponse() != null ? record.getResponse().length() : 0;
        }
        return chars;
    }

    private void saveChatRecord(User user, String sessionId, String message, String response,
                                List<ChatResponse.BookRecommendDTO> bookRecommendations) {
        AiChatRecord record = new AiChatRecord();
        record.setUserId(user.getId());
        record.setUserRole(user.getRole());
        record.setSessionId(sessionId);
        record.setMessage(message);
        record.setResponse(response);

        // 保存推荐的书籍ID
        if (bookRecommendations != null && !bookRecommendations.isEmpty()) {
            String bookIds = bookRecommendations.stream()
                    .map(b -> b.getId().toString())
                    .collect(Collectors.joining(","));
            record.setBookRecommendations(bookIds);
        }

        aiChatRecordMapper.insert(record);
    }

    private String callAi(String message, List<AiChatRecord> history, String systemPrompt) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统消息（包含真实图书列表）
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            // 添加历史消息
            for (AiChatRecord record : history) {
                // 用户消息
                Map<String, String> userMsg = new HashMap<>();
                userMsg.put("role", "user");
                userMsg.put("content", record.getMessage());
                messages.add(userMsg);

                // AI 回复
                Map<String, String> assistantMsg = new HashMap<>();
                assistantMsg.put("role", "assistant");
                assistantMsg.put("content", record.getResponse());
                messages.add(assistantMsg);
            }

            // 当前用户消息
            Map<String, String> currentUserMsg = new HashMap<>();
            currentUserMsg.put("role", "user");
            currentUserMsg.put("content", message);
            messages.add(currentUserMsg);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 1500);
            requestBody.put("temperature", 0.7);

            // 获取 baseUrl
            String baseUrl = getBaseUrl();

            // 构建请求
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(requestBody),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + aiConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("AI 请求失败: " + response);
                }

                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                // 提取 AI 回复
                return jsonResponse.path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText();
            }

        } catch (IOException e) {
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage());
        }
    }

    private String getBaseUrl() {
        String provider = aiConfig.getProvider();
        String baseUrl = aiConfig.getBaseUrl();

        // 如果配置了 baseUrl，直接使用
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }

        // 根据 provider 返回默认 baseUrl
        switch (provider) {
            case "qwen":
                return "https://dashscope.aliyuncs.com/compatible-mode/v1";
            case "ernie":
                return "https://aip.baidubce.com/rpc/2.0/ai_custom/v1";
            case "chatglm":
                return "https://open.bigmodel.cn/api/paas/v4";
            case "deepseek":
                return "https://api.deepseek.com/v1";
            default:
                return "https://api.openai.com/v1";
        }
    }

    /**
     * 从 AI 回复中提取书籍推荐（根据书名匹配真实在馆图书）
     */
    private List<ChatResponse.BookRecommendDTO> extractBookRecommendationsFromReply(String reply, List<Book> availableBooks) {
        // 方法1：先尝试匹配【书籍推荐】标记
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("【书籍推荐】(.+?)(?:\\n|$)");
        java.util.regex.Matcher matcher1 = pattern1.matcher(reply);
        String bookNamesStr = null;

        if (matcher1.find()) {
            bookNamesStr = matcher1.group(1);
        } else {
            // 方法2：如果没有标记，直接从《》中提取书名
            java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("《([^》]+)》");
            java.util.regex.Matcher matcher2 = pattern2.matcher(reply);
            List<String> bookNames = new ArrayList<>();
            while (matcher2.find()) {
                bookNames.add(matcher2.group(1));
            }
            if (!bookNames.isEmpty()) {
                bookNamesStr = String.join(",", bookNames);
            }
        }

        if (bookNamesStr == null || bookNamesStr.isEmpty()) {
            return null;
        }

        // 分割书名（用逗号、顿号或换行分隔）
        String[] bookNames = bookNamesStr.split("[，,\n]");

        // 构建书名到 Book 的映射（用于精确匹配）
        Map<String, Book> bookMap = new HashMap<>();
        for (Book book : availableBooks) {
            bookMap.put(book.getTitle(), book);
        }

        List<ChatResponse.BookRecommendDTO> recommendations = new ArrayList<>();
        Set<Long> addedBookIds = new HashSet<>();  // 用于去重

        for (String bookName : bookNames) {
            bookName = bookName.trim();
            if (bookName.isEmpty()) continue;

            // 精确匹配
            Book book = bookMap.get(bookName);

            // 如果没找到，尝试模糊匹配（包含关系）
            if (book == null) {
                for (Map.Entry<String, Book> entry : bookMap.entrySet()) {
                    if (entry.getKey().contains(bookName) || bookName.contains(entry.getKey())) {
                        book = entry.getValue();
                        break;
                    }
                }
            }

            if (book != null) {
                // 去重：检查是否已经添加过这本书
                if (addedBookIds.contains(book.getId().longValue())) {
                    continue;
                }
                addedBookIds.add(book.getId().longValue());

                ChatResponse.BookRecommendDTO dto = new ChatResponse.BookRecommendDTO();
                dto.setId(book.getId().longValue());
                dto.setTitle(book.getTitle());
                dto.setAuthor(book.getAuthor());
                dto.setIsbn(book.getIsbn());
                dto.setPublisher(book.getPublisher());
                dto.setTotalCopies(book.getStock());
                dto.setAvailableCopies(book.getStock());
                dto.setCover(book.getDescription());  // 借用字段存放封面URL或描述
                recommendations.add(dto);
            }
        }

        return recommendations.isEmpty() ? null : recommendations;
    }

    private String cleanResponse(String reply) {
        // 移除【书籍推荐】标记，但保留其中的书名
        return reply.replaceAll("【书籍推荐】[^\n]*", "").trim();
    }

    public int getRemainingCount(String username) {
        User user = userMapper.findByUsername(username);
        LocalDate today = LocalDate.now();

        if (user.getAiChatDate() == null || !user.getAiChatDate().equals(today)) {
            return aiConfig.getDailyLimit();
        }

        return Math.max(0, aiConfig.getDailyLimit() - user.getAiChatCount());
    }

    /**
     * 获取会话历史记录（需要验证用户权限）
     */
    public List<Map<String, Object>> getHistory(String sessionId, Integer userId) {
        // 安全验证：只允许用户访问自己的会话记录
        List<AiChatRecord> records = aiChatRecordMapper.findBySessionIdAndUserId(sessionId, userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (AiChatRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("role", "user");
            item.put("content", record.getMessage());
            result.add(item);

            Map<String, Object> assistantItem = new HashMap<>();
            assistantItem.put("role", "assistant");
            assistantItem.put("content", record.getResponse());
            result.add(assistantItem);
        }
        return result;
    }
}
