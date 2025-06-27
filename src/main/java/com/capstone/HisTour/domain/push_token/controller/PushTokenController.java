package com.capstone.HisTour.domain.push_token.controller;

import com.capstone.HisTour.domain.push_token.dto.PushTokenRequest;
import com.capstone.HisTour.domain.push_token.service.PushTokenService;
import com.capstone.HisTour.domain.apiPayload.DefaultResponse;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pushToken")
@RequiredArgsConstructor
public class PushTokenController {

    private final PushTokenService pushTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<DefaultResponse<String>> saveOrUpdatePushToken(@RequestHeader(value = "Authorization", required = false) String token,
                                                                         @RequestBody PushTokenRequest pushTokenRequest) {

        Long memberId = null;

        if (token != null && token.startsWith("Bearer ")) {
            memberId = getMemberIdFromToken(token);
        }

        pushTokenService.saveOrUpdatePushToken(pushTokenRequest, memberId);

        DefaultResponse<String> response = DefaultResponse.response(
                "push token 등록/업데이트 성공"
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
