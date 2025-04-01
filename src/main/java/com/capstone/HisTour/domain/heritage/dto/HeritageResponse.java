package com.capstone.HisTour.domain.heritage.dto;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.region.dto.RegionResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HeritageResponse {
    private Long id;
    private String name;
    private String category;
    private String detailAddress;
    private String description;
    private RegionResponse region;
    private double latitude;
    private double longitude;

    // Heritage -> HeritageResponse 변환
    public static HeritageResponse from(Heritage heritage) {
        return HeritageResponse.builder()
                .id(heritage.getId())
                .name(heritage.getName())
                .category(heritage.getCategory())
                .detailAddress(heritage.getDetailAddress())
                .description(heritage.getDescription())
                .region(RegionResponse.from(heritage.getRegion()))
                .latitude(heritage.getGeom().getY())
                .longitude(heritage.getGeom().getX())
                .build();
    }
}
