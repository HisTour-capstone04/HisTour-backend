package com.capstone.HisTour.domain.alarm.controller;

import com.capstone.HisTour.domain.alarm.dto.AlarmRequest;
import com.capstone.HisTour.domain.alarm.service.AlarmService;
import com.capstone.HisTour.global.DefaultResponse;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send")
    public ResponseEntity<DefaultResponse<String>> sendNotification(
            @RequestHeader(name = "Authorization", required = false) String token,
            @RequestBody AlarmRequest alarmRequest) {

        Long memberId = null;
        if (token != null) {
            memberId = getMemberIdFromToken(token);
        }
        alarmService.sendPushNotification(memberId, alarmRequest);

        DefaultResponse<String> response = DefaultResponse.response(
                "알람 전송 완료"
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
