package com.capstone.HisTour.domain.region.dto;

import com.capstone.HisTour.domain.region.domain.Region;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResponse {
    private Long id;
    private String city;
    private String district;
    private String road;

    public static RegionResponse from(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .city(region.getCity())
                .district(region.getDistrict())
                .road(region.getRoad())
                .build();
    }
}
