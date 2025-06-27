package com.capstone.HisTour.domain.chatbot.controller;

import com.capstone.HisTour.domain.chatbot.dto.ChatRequest;
import com.capstone.HisTour.domain.chatbot.dto.ChatbotResponse;
import com.capstone.HisTour.domain.chatbot.service.ChatbotService;
import com.capstone.HisTour.domain.apiPayload.DefaultResponse;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<DefaultResponse<ChatbotResponse>> askChatbot(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody ChatRequest chatRequest) {

        // memberId 추출
        long memberId = getMemberIdFromToken(token);

        ChatbotResponse result = chatbotService.askChatbot(memberId, chatRequest);

        DefaultResponse<ChatbotResponse> response = DefaultResponse.response(
                "챗봇 답변 성공",
                result
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    private Long getMemberIdFromToken(String token) {

        // jwt token에서 claim 추출
        String accessToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseJwtToken(accessToken);

        // member id 추출
        return claims.get("memberId", Long.class);
    }
}
