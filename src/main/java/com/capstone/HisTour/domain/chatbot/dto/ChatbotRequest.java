package com.capstone.HisTour.domain.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ChatbotRequest {

    @JsonProperty("user_id")
    private Long userId;
    private String question;
    private Double latitude;
    private Double longitude;
}
