package com.capstone.HisTour.domain.chatbot.service;

import com.capstone.HisTour.domain.chatbot.domain.ChatHistory;
import com.capstone.HisTour.domain.chatbot.dto.ChatRequest;
import com.capstone.HisTour.domain.chatbot.dto.ChatbotResponse;
import com.capstone.HisTour.domain.chatbot.repository.ChatHistoryRepository;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final MemberRepository memberRepository;
    private final RestClient restClient;

    @Value("${chatbot.url}")
    private String chatbotUrl;

    public ChatbotResponse askChatbot(Long memberId, ChatRequest chatRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        String answer = getResponseFromChatbot(chatRequest);

        ChatHistory chatHistory = ChatHistory.builder()
                .member(member)
                .question(chatRequest.getQuestion())
                .answer(answer)
                .timestamp(LocalDateTime.now())
                .build();

        chatHistoryRepository.save(chatHistory);

        return ChatbotResponse.from(answer);

    }

    private String getResponseFromChatbot(ChatRequest chatRequest) {

        String question = chatRequest.getQuestion();

        return restClient.post()
                .uri(chatbotUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(question)
                .retrieve()
                .body(String.class);
    }
}
