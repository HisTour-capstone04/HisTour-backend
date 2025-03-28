package com.capstone.HisTour.domain.member.controller;

import com.capstone.HisTour.domain.member.dto.LoginRequest;
import com.capstone.HisTour.domain.member.dto.LoginResponse;
import com.capstone.HisTour.domain.member.dto.SignupRequest;
import com.capstone.HisTour.domain.member.service.MemberService;
import com.capstone.HisTour.global.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<DefaultResponse<String>> signup(@RequestBody SignupRequest signupRequest) {

        //회원가입 처리 로직
        memberService.signUp(signupRequest);

        // DefaultResponseDto 생성
        DefaultResponse<String> response = DefaultResponse.response(
                "회원 가입이 완료되었습니다."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {

        // 로그인 처리 로직
        LoginResponse loginResponse = memberService.login(loginRequest);

        // DefaultResponseDto 생성
        DefaultResponse<LoginResponse> response = DefaultResponse.response(
                "로그인에 성공했습니다 " + loginResponse.getUsername() + "님",
                loginResponse
        );

        // HTTP 200 + 응답 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 소셜 로그인

    // 사용자 정보 조회
}
