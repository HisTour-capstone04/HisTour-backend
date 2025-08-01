package com.capstone.HisTour.domain.heritage.controller;

import com.capstone.HisTour.domain.heritage.dto.HeritageListResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageRecommendListResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.domain.apiPayload.DefaultResponse;
import com.capstone.HisTour.global.annotation.MeasureExecutionTime;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/heritages")
@RequiredArgsConstructor
@Slf4j
public class HeritageController {

    private final HeritageService heritageService;
    private final JwtTokenProvider jwtTokenProvider;

    // 특정 유적지 조회
    @GetMapping("/{id}")
    @MeasureExecutionTime
    public DefaultResponse<HeritageResponse> getHeritageById(@PathVariable Long id) {

        // 유적지 조회
        HeritageResponse heritageResponse = heritageService.getHeritageById(id);

        return DefaultResponse.onSuccess(heritageResponse);
    }

    // 근처 유적지 조회
    @GetMapping("/nearby")
    @MeasureExecutionTime
    public DefaultResponse<HeritageListResponse> getHeritageNearby(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") double radius) {

        // memberId 추출
        Long memberId = getMemberIdFromToken(token);

        // 근처 유적지 조회
        HeritageListResponse heritageResponses = heritageService.getHeritageNearby(latitude, longitude, radius);

        return DefaultResponse.onSuccess(heritageResponses);
    }

    // 근처 유적지 조회 후
    // '(가장 가까운 유적지 이름) + (나머지 유적지 개수)' 알람 메시지 반환
    @GetMapping("/nearby-for-alarm")
    @MeasureExecutionTime
    public DefaultResponse<HeritageListResponse> getHeritageNearbyForAlarm(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") double radius) {

        // memberId 추출
        Long memberId = getMemberIdFromToken(token);

        // 근처 유적지 조회
        HeritageListResponse heritageResponses = heritageService.getHeritageNearbyForAlarm(memberId, latitude, longitude, radius);

        return DefaultResponse.onSuccess(heritageResponses);
    }

    // 유적지 검색
    @GetMapping
    @MeasureExecutionTime
    public DefaultResponse<HeritageListResponse> searchHeritagesByName(@RequestParam String name) {

        // 이름으로 유적지 조회
        HeritageListResponse heritageListResponse = heritageService.searchHeritageByName(name);

        return DefaultResponse.onSuccess(heritageListResponse);
    }

    // 경로 사이에 있는 유적지 조회
    @GetMapping("/in-path")
    public DefaultResponse<HeritageListResponse> searchHeritagesInRoute(
            @RequestParam Double srcLatitude,
            @RequestParam Double srcLongitude,
            @RequestParam Double destLatitude,
            @RequestParam Double destLongitude) {

        // 경로상에 있는 유적지 조회
        HeritageListResponse heritagesInRoute = heritageService.searchHeritageInRoute(srcLatitude, srcLongitude, destLatitude, destLongitude);

        return DefaultResponse.onSuccess(heritagesInRoute);
    }

    // 유적지 추천
    @GetMapping("/recommend")
    public DefaultResponse<HeritageRecommendListResponse> recommendHeritages(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        Long memberId = getMemberIdFromToken(token);

        HeritageRecommendListResponse recommendedHeritages =  heritageService.recommendHeritages(memberId, latitude, longitude);

        return DefaultResponse.onSuccess(recommendedHeritages);
    }

    private Long getMemberIdFromToken(String token) {

        // jwt token에서 claim 추출
        String accessToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseJwtToken(accessToken);

        // member id 추출
        return claims.get("memberId", Long.class);
    }

}
