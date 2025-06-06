package com.capstone.HisTour.domain.heritage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HeritageRecommendListResponse {
    private int count;
    private List<HeritageRecommendResponse> recommendations;
}
