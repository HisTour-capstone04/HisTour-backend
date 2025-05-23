package com.capstone.HisTour.domain.bookmark.dto;

import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookmarkListResponse {
    private int count;
    private List<HeritageResponse> heritages;

    public static BookmarkListResponse from(List<HeritageResponse> heritageResponses) {
        return BookmarkListResponse.builder()
                .count(heritageResponses.size())
                .heritages(heritageResponses)
                .build();
    }
}
