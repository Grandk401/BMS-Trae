package com.bms.mapper;

import com.bms.entity.AiChatRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI 聊天记录 Mapper
 */
@Mapper
public interface AiChatRecordMapper {

    /**
     * 插入聊天记录
     */
    @Insert("INSERT INTO ai_chat_record(user_id, user_role, session_id, message, response, book_recommendations, token_used) " +
            "VALUES(#{userId}, #{userRole}, #{sessionId}, #{message}, #{response}, #{bookRecommendations}, #{tokenUsed})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AiChatRecord record);

    /**
     * 根据用户ID查询聊天记录（分页）
     */
    @Select("SELECT * FROM ai_chat_record WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<AiChatRecord> findByUserId(@Param("userId") Integer userId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据会话ID查询聊天记录
     */
    @Select("SELECT * FROM ai_chat_record WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<AiChatRecord> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID和用户ID查询聊天记录（安全验证）
     */
    @Select("SELECT * FROM ai_chat_record WHERE session_id = #{sessionId} AND user_id = #{userId} ORDER BY created_at ASC")
    List<AiChatRecord> findBySessionIdAndUserId(@Param("sessionId") String sessionId, @Param("userId") Integer userId);

    /**
     * 根据会话ID查询最近的聊天记录（用于截断上下文）
     */
    @Select("SELECT * FROM ai_chat_record WHERE session_id = #{sessionId} ORDER BY created_at DESC LIMIT #{limit}")
    List<AiChatRecord> findRecentBySessionId(@Param("sessionId") String sessionId, @Param("limit") int limit);

    /**
     * 统计用户聊天记录总数
     */
    @Select("SELECT COUNT(*) FROM ai_chat_record WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 根据会话ID统计记录数
     */
    @Select("SELECT COUNT(*) FROM ai_chat_record WHERE session_id = #{sessionId}")
    int countBySessionId(@Param("sessionId") String sessionId);

    /**
     * 删除用户的聊天记录
     */
    @Delete("DELETE FROM ai_chat_record WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Integer userId);

    /**
     * 删除会话的所有记录
     */
    @Delete("DELETE FROM ai_chat_record WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
