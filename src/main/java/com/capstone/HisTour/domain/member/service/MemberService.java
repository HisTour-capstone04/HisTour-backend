package com.capstone.HisTour.domain.member.service;

import com.capstone.HisTour.domain.apiPayload.exception.handler.MemberHandler;
import com.capstone.HisTour.domain.apiPayload.status.ErrorStatus;
import com.capstone.HisTour.domain.member.domain.LoginType;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.domain.MemberStatus;
import com.capstone.HisTour.domain.member.dto.LoginRequest;
import com.capstone.HisTour.domain.member.dto.LoginResponse;
import com.capstone.HisTour.domain.member.dto.MemberResponse;
import com.capstone.HisTour.domain.member.dto.SignupRequest;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.refresh_token.domain.RefreshToken;
import com.capstone.HisTour.domain.refresh_token.repository.RefreshTokenRepository;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입 로직
    @Transactional
    public MemberResponse signUp(SignupRequest signupRequest) {

        // signupRequest validation 확인
        Member member = registerValid(signupRequest);

        // member DB에 저장
        return MemberResponse.from(memberRepository.save(member));
    }

    // 로그인 로직
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        // 로그인 유효 검사
        Member member = loginValid(loginRequest);

        // refresh token 생성
        // DB에 저장되어 있는 refresh token을 먼저 조회
        // 없다면 refresh token을 생성하고 DB에 저장
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByMemberId(member.getId())
                .orElseGet(() -> refreshTokenRepository.save(RefreshToken.builder()
                        .member(member)
                        .token(jwtTokenProvider.createRefreshToken(member))
                        .build()));

        // access token 생성
        String accessToken = jwtTokenProvider.createAccessToken(member, refreshTokenEntity.getId());


        // LoginResponse dto 생성
        return LoginResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .accessToken(accessToken)
                .build();
    }

    // 회원정보 조회 로직
    public MemberResponse getMemberInfo(String authHeader) {

        // header의 Bearer 제거
        String token = authHeader.substring(7);

        // JWT 토큰 파싱
        Claims claims = jwtTokenProvider.parseJwtToken(token);

        // 유저 조회
        Member member = memberRepository.findById(claims.get("memberId", Long.class))
                .orElseThrow(() -> new RuntimeException("member id가 유효하지 않습니다."));

        return MemberResponse.from(member);
    }

    // 회원가입 유효 검사
    private Member registerValid(SignupRequest signupRequest) {

        // 해당 이메일이 존재하면 회원가입 실패
        if (memberRepository.existsByEmail(signupRequest.getEmail()))
            throw new MemberHandler(ErrorStatus.EMAIL_DUPLICATE);

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
                .orElseThrow(() -> new MemberHandler(ErrorStatus.LOGIN_NOT_VALID));

        // 조회된 유저의 비밀번호와 signupRequest의 비밀번호가 일치하는지 검증
        if (!member.getPassword().equals(loginRequest.getPassword()))
            throw new MemberHandler(ErrorStatus.LOGIN_NOT_VALID);

        return member;
    }
}
