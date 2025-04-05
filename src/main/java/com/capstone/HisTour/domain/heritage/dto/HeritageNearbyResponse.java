package com.capstone.HisTour.domain.heritage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HeritageNearbyResponse {
    private int count;
    private List<HeritageResponse> heritages;
}
