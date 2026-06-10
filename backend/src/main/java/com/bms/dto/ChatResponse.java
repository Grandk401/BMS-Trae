package com.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String reply;
    private List<BookRecommendDTO> bookRecommendations;
    private String sessionId;  // 会话ID

    public ChatResponse(String reply) {
        this.reply = reply;
        this.bookRecommendations = null;
        this.sessionId = null;
    }

    public ChatResponse(String reply, List<BookRecommendDTO> bookRecommendations) {
        this.reply = reply;
        this.bookRecommendations = bookRecommendations;
        this.sessionId = null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookRecommendDTO {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private String publisher;
        private Integer totalCopies;
        private Integer availableCopies;
        private String cover;
        private String description;
    }
}
