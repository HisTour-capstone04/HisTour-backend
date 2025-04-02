package com.capstone.HisTour.domain.heritage.controller;

import com.capstone.HisTour.domain.heritage.dto.HeritageNearbyResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.global.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heritages")
@RequiredArgsConstructor
public class HeritageController {

    private final HeritageService heritageService;

    // 특정 유적지 조회
    @GetMapping("/{id}")
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
    public ResponseEntity<DefaultResponse<HeritageNearbyResponse>> getHeritageNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") double radius) {

        // 근처 유적지 조회
        HeritageNearbyResponse heritageResponses = heritageService.getHeritageNearby(latitude, longitude, radius);

        // ResponseDto 생성
        DefaultResponse<HeritageNearbyResponse> response = DefaultResponse.response(
                "근처 유적지 조회 성공",
                heritageResponses
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
