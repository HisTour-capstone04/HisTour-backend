package com.capstone.HisTour.domain.heritage.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HeritageListResponse {
    private int count;
    @Nullable
    private String message;
    private List<HeritageResponse> heritages;
}
