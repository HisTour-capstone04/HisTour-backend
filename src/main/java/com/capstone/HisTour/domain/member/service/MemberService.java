package com.capstone.HisTour.domain.member.service;

import com.capstone.HisTour.domain.member.domain.LoginType;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.domain.MemberStatus;
import com.capstone.HisTour.domain.member.dto.LoginRequest;
import com.capstone.HisTour.domain.member.dto.LoginResponse;
import com.capstone.HisTour.domain.member.dto.SignupRequest;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.refresh_token.domain.RefreshToken;
import com.capstone.HisTour.domain.refresh_token.repository.RefreshTokenRepository;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입 로직
    @Transactional
    public void signUp(SignupRequest signupRequest) {

        // signupRequest validation 확인
        Member member = registerValid(signupRequest);

        // member DB에 저장
        memberRepository.save(member);
    }

    // 로그인 로직
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        // 로그인 유효 검사
        Member member = loginValid(loginRequest);

        // access token 생성
        String accessToken = jwtTokenProvider.createAccessToken(member);

        // refresh token 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        // refresh token DB에 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .member(member)
                .token(refreshToken)
                .build());

        // LoginResponse dto 생성
        return LoginResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .accessToken(accessToken)
                .build();
    }

    // 회원가입 유효 검사
    private Member registerValid(SignupRequest signupRequest) {

        // 해당 이메일이 존재하면 회원가입 실패
        if (memberRepository.existsByEmail(signupRequest.getEmail()))
            throw new RuntimeException();

        return Member.builder()
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .username(signupRequest.getUsername())
                .loginType(LoginType.REGULAR)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    // 로그인 유효 검사
    private Member loginValid(LoginRequest loginRequest) {

        // 이메일을 통해서 유저가 존재하는지 확인
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일, 비밀번호입니다."));

        // 조회된 유저의 비밀번호와 signupRequest의 비밀번호가 일치하는지 검증
        if (!member.getPassword().equals(loginRequest.getPassword()))
            throw new RuntimeException();

        return member;
    }
}
