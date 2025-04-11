package com.capstone.HisTour.domain.bookmark.dto;

import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookmarkResponse {
    private Long id;
    private Long memberId;
    private Long heritageId;
    private LocalDateTime createdAt;

    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .memberId(bookmark.getMember().getId())
                .heritageId(bookmark.getHeritage().getId())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
