package com.capstone.HisTour.domain.visited.controller;

import com.capstone.HisTour.domain.visited.dto.VisitedListResponse;
import com.capstone.HisTour.domain.visited.dto.VisitedRequest;
import com.capstone.HisTour.domain.visited.service.VisitedService;
import com.capstone.HisTour.domain.apiPayload.DefaultResponse;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visited")
public class VisitedController {

    private final VisitedService visitedService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<DefaultResponse<String>> addVisited(@RequestHeader(value = "Authorization") String token,
                                                              @RequestBody VisitedRequest visitedRequest) {
        // memberId 추출
        Long memberId = getMemberIdFromToken(token);

        // service 위임
        visitedService.addVisited(memberId, visitedRequest);

        // DefaultResponseDto 생성
        DefaultResponse<String> response = DefaultResponse.response(
                "방문지 등록 완료"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<VisitedListResponse>> getVisitedList(@RequestHeader(value = "Authorization") String token) {

        // memberId 추출
        Long memberId = getMemberIdFromToken(token);

        // service 위임
        VisitedListResponse visitedListResponse = visitedService.getVisitedList(memberId);

        DefaultResponse<VisitedListResponse> response = DefaultResponse.response(
                "방문지 조회 완료",
                visitedListResponse
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
