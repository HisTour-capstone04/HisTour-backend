package com.capstone.HisTour.domain.bookmark.controller;

import com.capstone.HisTour.domain.bookmark.dto.BookmarkRequest;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkResponse;
import com.capstone.HisTour.domain.bookmark.service.BookmarkService;
import com.capstone.HisTour.global.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 북마크 추가
    @PostMapping
    public ResponseEntity<DefaultResponse<BookmarkResponse>> addBookmark(@RequestBody BookmarkRequest request) {

        // 북마크 추가 로직
        BookmarkResponse bookmarkResponse = bookmarkService.addBookmark(request);

        // DefaultResponseDto 생성
        DefaultResponse<BookmarkResponse> response = DefaultResponse.response(
                "북마크 등록 완료.",
                bookmarkResponse
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
