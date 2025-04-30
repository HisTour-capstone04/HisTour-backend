package com.capstone.HisTour.domain.visited.dto;

import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VisitedListResponse {
    private int count;
    private List<HeritageResponse> heritages;

    public static VisitedListResponse from(List<HeritageResponse> heritageResponses) {
        return VisitedListResponse.builder()
                .count(heritageResponses.size())
                .heritages(heritageResponses)
                .build();
    }
}
