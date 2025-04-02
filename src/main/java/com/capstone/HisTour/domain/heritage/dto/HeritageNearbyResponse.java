package com.capstone.HisTour.domain.heritage.dto;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HeritageNearbyResponse {
    private int count;
    private List<HeritageResponse> heritages;

    public static HeritageNearbyResponse from(List<Heritage> heritages) {
        return HeritageNearbyResponse.builder()
                .count(heritages.size())
                .heritages(heritages.stream().map(HeritageResponse::from).toList())
                .build();
    }
}
