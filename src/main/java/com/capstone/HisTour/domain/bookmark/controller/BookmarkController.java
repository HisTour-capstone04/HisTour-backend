package com.capstone.HisTour.domain.bookmark.controller;

import com.capstone.HisTour.domain.bookmark.dto.BookmarkListResponse;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkRequest;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkResponse;
import com.capstone.HisTour.domain.bookmark.service.BookmarkService;
import com.capstone.HisTour.global.DefaultResponse;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final JwtTokenProvider jwtTokenProvider;

    // 북마크 추가
    @PostMapping
    public ResponseEntity<DefaultResponse<BookmarkResponse>> addBookmark(@RequestHeader(value = "Authorization") String token,
                                                                         @RequestBody BookmarkRequest request) {

        // access token에서 member id 추출
        Long memberId = getMemberIdFromToken(token);

        // 북마크 추가 로직
        BookmarkResponse bookmarkResponse = bookmarkService.addBookmark(memberId, request);

        // DefaultResponseDto 생성
        DefaultResponse<BookmarkResponse> response = DefaultResponse.response(
                "북마크 등록 완료.",
                bookmarkResponse
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 북마크 리스트 조회
    @GetMapping
    public ResponseEntity<DefaultResponse<BookmarkListResponse>> getAllBookmarks(@RequestHeader(value = "Authorization") String token) {

        // access token에서 member id 추출
        Long memberId = getMemberIdFromToken(token);

        // 북마크 조회 로직
        BookmarkListResponse bookmarkListResponse = bookmarkService.getBookmarkList(memberId);

        // DefaultResponseDto 생성
        DefaultResponse<BookmarkListResponse> response = DefaultResponse.response(
                "북마크 조회 완료",
                bookmarkListResponse
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private Long getMemberIdFromToken(String token) {

        // jwt token에서 claim 추출
        String accessToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseJwtToken(accessToken);

        // member id 추출
        return claims.get("memberId", Long.class);
    }

}
