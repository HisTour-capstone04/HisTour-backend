package com.capstone.HisTour.domain.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatbotResponse {
    private String answer;

    public static ChatbotResponse from(String answer) {
        return ChatbotResponse.builder()
                .answer(answer)
                .build();
    }
}
