package com.capstone.HisTour.domain.heritage.controller;

import com.capstone.HisTour.domain.heritage.dto.HeritageListResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.global.DefaultResponse;
import com.capstone.HisTour.global.annotation.MeasureExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heritages")
@RequiredArgsConstructor
@Slf4j
public class HeritageController {

    private final HeritageService heritageService;

    // 특정 유적지 조회
    @GetMapping("/{id}")
    @MeasureExecutionTime
    public ResponseEntity<DefaultResponse<HeritageResponse>> getHeritageById(@PathVariable Long id) {

        // 유적지 조회
        HeritageResponse heritageResponse = heritageService.getHeritageById(id);

        // ResponseDto 생성
        DefaultResponse<HeritageResponse> response = DefaultResponse.response(
                "특정 heritage 조회 성공",
                heritageResponse
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 근처 유적지 조회
    @GetMapping("/nearby")
    @MeasureExecutionTime
    public ResponseEntity<DefaultResponse<HeritageListResponse>> getHeritageNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") double radius) {

        // 근처 유적지 조회
        HeritageListResponse heritageResponses = heritageService.getHeritageNearby(latitude, longitude, radius);

        // ResponseDto 생성
        DefaultResponse<HeritageListResponse> response = DefaultResponse.response(
                "근처 유적지 조회 성공",
                heritageResponses
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 유적지 검색
    @GetMapping
    @MeasureExecutionTime
    public ResponseEntity<DefaultResponse<HeritageListResponse>> searchHeritagesByName(@RequestParam String name) {

        // 이름으로 유적지 조회
        HeritageListResponse heritageListResponse = heritageService.searchHeritageByName(name);

        // ResponseDto 생성
        DefaultResponse<HeritageListResponse> response = DefaultResponse.response(
                "유적지 이름으로 조회 성공",
                heritageListResponse
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 경로 사이에 있는 유적지 조회
    @GetMapping("/in-path")
    public ResponseEntity<DefaultResponse<HeritageListResponse>> searchHeritagesInRoute(
            @RequestParam Double srcLatitude,
            @RequestParam Double srcLongitude,
            @RequestParam Double destLatitude,
            @RequestParam Double destLongitude) {

        // 경로상에 있는 유적지 조회
        HeritageListResponse heritagesInRoute = heritageService.searchHeritageInRoute(srcLatitude, srcLongitude, destLatitude, destLongitude);

        DefaultResponse<HeritageListResponse> response = DefaultResponse.response(
                "경로상에 있는 유적지 조회 성공",
                heritagesInRoute
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
