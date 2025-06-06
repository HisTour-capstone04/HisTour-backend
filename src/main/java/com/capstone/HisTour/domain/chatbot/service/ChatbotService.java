package com.capstone.HisTour.domain.chatbot.service;

import com.capstone.HisTour.domain.chatbot.domain.ChatHistory;
import com.capstone.HisTour.domain.chatbot.dto.ChatRequest;
import com.capstone.HisTour.domain.chatbot.dto.ChatbotRequest;
import com.capstone.HisTour.domain.chatbot.dto.ChatbotResponse;
import com.capstone.HisTour.domain.chatbot.repository.ChatHistoryRepository;
import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.global.annotation.MeasureExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final HeritageRepository heritageRepository;
    private final MemberRepository memberRepository;
    private final RestClient restClient;

    @Value("${chatbot.url}")
    private String chatbotUrl;

    @Transactional
    @MeasureExecutionTime
    public ChatbotResponse askChatbot(Long memberId, ChatRequest chatRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        String title = extractHeritageName(chatRequest.getQuestion());

        ChatbotResponse answer = getResponseFromChatbot(chatRequest, member.getId());

        ChatHistory chatHistory = ChatHistory.builder()
                .member(member)
                .question(chatRequest.getQuestion())
                .answer(answer.getAnswer())
                .timestamp(LocalDateTime.now())
                .build();

        if (title != null) {
            chatHistory.setTitle(title);
        }

        chatHistoryRepository.save(chatHistory);

        return ChatbotResponse.from(answer.getAnswer(), title);
    }

    private ChatbotResponse getResponseFromChatbot(ChatRequest chatRequest, Long memberId) {

        String question = chatRequest.getQuestion();

        ChatbotRequest.ChatbotRequestBuilder builder = ChatbotRequest.builder()
                .userId(""+memberId)
                .question(question);

        if (chatRequest.getLatitude() != null) {
            builder.latitude(chatRequest.getLatitude());
        }

        if (chatRequest.getLongitude() != null) {
            builder.longitude(chatRequest.getLongitude());
        }

        ChatbotRequest chatbotRequest = builder.build();

        return restClient.post()
                .uri(chatbotUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(chatbotRequest)
                .retrieve()
                .body(ChatbotResponse.class);
    }

    private String extractHeritageName(String question) {
        // "에 대해서", "에 대해", "에 관한", "에 관해" 등을 기준으로 앞부분만 추출
        String[] splitKeywords = {"에 대해서", "에 대해", "에 관한", "에 관해", "에 대해 알려줘", "에 대해 설명해줘"};

        String title = null;

        for (String keyword : splitKeywords) {
            if (question.contains(keyword)) {
                title = question.split(keyword)[0].trim();
            }
        }

        // 위 키워드가 없는 경우 fallback: 마지막 조사 기준으로 나누기
        if (question.endsWith("에 대해 설명해줘") || question.endsWith("에 대해 알려줘")) {
            title = question.substring(0, question.indexOf("에 대해")).trim();
        }

        List<Heritage> heritages = heritageRepository.findByNameContainingIgnoreCase(title);
        Heritage foundHeritage = null;
        if (!heritages.isEmpty()) {
            foundHeritage = heritages.get(0);
        }
        if (foundHeritage != null) {
            return foundHeritage.getName();
        } else {
            return null;
        }
    }
}
