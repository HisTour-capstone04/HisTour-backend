package com.capstone.HisTour.domain.heritage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HeritageListResponse {
    private int count;
    private List<HeritageResponse> heritages;
}
