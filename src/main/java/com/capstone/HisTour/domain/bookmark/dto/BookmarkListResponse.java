package com.capstone.HisTour.domain.bookmark.dto;

import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;

@Getter
@Builder
public class BookmarkListResponse {
    private int count;
    private List<HeritageResponse> heritages;

//    public static BookmarkListResponse from(List<Bookmark> bookmarks, List<List<String>> imageUrls) {
//        return BookmarkListResponse.builder()
//                .count(bookmarks.size())
//                .heritages(
//                        IntStream.range(0, bookmarks.size())
//                                .mapToObj(i -> HeritageResponse.from(bookmarks.get(i).getHeritage(), imageUrls.get(i)))
//                                .toList())
//                .build();
//    }

    public static BookmarkListResponse from(List<HeritageResponse> heritageResponses) {
        return BookmarkListResponse.builder()
                .count(heritageResponses.size())
                .heritages(heritageResponses)
                .build();
    }
}
