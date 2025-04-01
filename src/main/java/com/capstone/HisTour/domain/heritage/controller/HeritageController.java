package com.capstone.HisTour.domain.heritage.controller;

import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.global.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // 유적지 검색
}
