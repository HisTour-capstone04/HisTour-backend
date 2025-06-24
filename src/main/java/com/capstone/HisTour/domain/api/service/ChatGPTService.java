package com.capstone.HisTour.domain.api.service;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    @Value("${api.openai.api-key}")
    String openAIKey;

    private final RestClient restClient;

    public Map<Long, String> recommendFromContext(List<Heritage> visited,
                                               List<Heritage> bookmarked,
                                               List<Heritage> candidates) {

        // 프롬프트 생성
        String prompt = buildPrompt(visited, bookmarked, candidates);

        // OpenAI API 호출 및 Map<id:추천이유> 반환
       return callOpenAI(prompt);
    }

    private String buildPrompt(List<Heritage> visited, List<Heritage> bookmarked, List<Heritage> candidates) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 한국 문화유산 추천 전문가입니다.\n");

        if (visited.isEmpty()) {
            sb.append("사용자는 아직 방문한 유적지가 없습니다.\n");
        } else {
            sb.append("사용자는 이전에 다음과 같은 유적지를 방문했습니다:\n");
            for (Heritage heritage : visited) {
                sb.append("- ").append(heritage.toString()).append("\n");
            }
        }

        if (bookmarked.isEmpty()) {
            sb.append("\n사용자는 북마크한 유적지가 없습니다.\n");
        } else {
            sb.append("\n사용자는 다음 유적지를 북마크했습니다:\n");
            for (Heritage heritage : bookmarked) {
                sb.append("- ").append(heritage.toString()).append("\n");
            }
        }

        sb.append("\n아래는 추천 후보로 고려할 수 있는 유적지 목록입니다:\n");
        for (Heritage heritage : candidates) {
            sb.append("- ").append(heritage.toString()).append("\n");
        }

        sb.append("""
        이제 후보 유적지 중에서 반드시 최대 5개를 추천해주세요.
        방문했던 유적지와 **역사적·시대적·지리적으로 관련 있는 유적지를 우선적으로 추천**하고, 북마크한 유적지도 고려해주세요. 
        사용자가 아무 유적지도 방문하지 않았거나 북마크하지 않았다면, **후보 유적지 중 역사적으로 가치 있는 유적지**를 무작위로 골라 그 이유를 설명해주세요.
        
        
        반드시 다음 형식으로 JSON 배열 형식으로만 응답해주세요:
        [
          {
            "id": 1234,
            "reason": "이 유적지는 경복궁과 같은 조선 시대 궁궐로, 사용자가 이전에 방문한 유적지와 역사적 배경이 유사합니다."
          },
          ...
        ]
        
        반드시 한국어로 작성해주세요. 너무 형식적이지 않은, 자연스러운 설명을 부탁드립니다.
        """);

        return sb.toString();
    }

    private Map<Long, String> callOpenAI(String prompt) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> body = Map.of(
                "model", "gpt-4",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7
        );

        JsonNode response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + openAIKey)
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(JsonNode.class);


        String content = response
                .path("choices").get(0)
                .path("message").path("content").asText();

        System.out.println(content);

        Map<Long, String> recommendations = new LinkedHashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arrayNode = mapper.readTree(content);

            if (arrayNode.isArray()) {
                for (JsonNode obj : arrayNode) {
                    long id = obj.path("id").asLong();
                    String reason = obj.path("reason").asText();
                    if (id != 0 && reason != null && !reason.isBlank()) {
                        recommendations.put(id, reason);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse OpenAI response: " + e.getMessage());
        }

        return recommendations;
    }
}
