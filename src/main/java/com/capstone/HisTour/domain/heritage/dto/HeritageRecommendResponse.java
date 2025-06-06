package com.capstone.HisTour.domain.heritage.dto;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.region.dto.RegionResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HeritageRecommendResponse {
    private Long id;
    private String name;
    private String category;
    private String detailAddress;
    private String description;
    private RegionResponse region;
    private double latitude;
    private double longitude;
    private List<String> imageUrls;
    private String era;
    private String side;
    private String type;
    private String recommendReason;

    public static HeritageRecommendResponse from(Heritage heritage, List<String> imageUrls, String recommendReason) {
        return HeritageRecommendResponse.builder()
                .id(heritage.getId())
                .name(heritage.getName())
                .category(heritage.getCategory())
                .detailAddress(heritage.getDetailAddress())
                .description(heritage.getDescription())
                .region(RegionResponse.from(heritage.getRegion()))
                .latitude(heritage.getGeom().getY())
                .longitude(heritage.getGeom().getX())
                .imageUrls(imageUrls)
                .era(heritage.getEra())
                .side(heritage.getSide())
                .type(heritage.getType())
                .recommendReason(recommendReason)
                .build();
    }
}
