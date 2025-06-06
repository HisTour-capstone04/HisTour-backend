package com.capstone.HisTour.domain.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatbotResponse {
    private String answer;
    private String title;

    public static ChatbotResponse from(String answer, String title) {
        return ChatbotResponse.builder()
                .answer(answer)
                .title(title == null ? "" : title)
                .build();
    }
}
