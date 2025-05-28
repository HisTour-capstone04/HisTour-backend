package com.capstone.HisTour.domain.chatbot.dto;

import lombok.Getter;

@Getter
public class ChatRequest {
    private String userId;
    private String question;
    private Double latitude;
    private Double longitude;
}
