package com.capstone.HisTour.domain.visited.dto;

import com.capstone.HisTour.domain.visited.domain.Visited;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitedResponse {
    private Long id;
    private Long memberId;
    private Long heritageId;
    private LocalDateTime createdAt;

    public static VisitedResponse from(Visited visited) {
        return VisitedResponse.builder()
                .id(visited.getId())
                .memberId(visited.getMember().getId())
                .heritageId(visited.getHeritage().getId())
                .createdAt(visited.getCreatedAt())
                .build();
    }
}
