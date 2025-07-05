package com.capstone.HisTour.member;

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
import com.capstone.HisTour.domain.member.service.MemberService;
import com.capstone.HisTour.domain.refresh_token.domain.RefreshToken;
import com.capstone.HisTour.domain.refresh_token.repository.RefreshTokenRepository;
import com.capstone.HisTour.global.auth.jwt.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() {

        // given
        SignupRequest signupRequest = new SignupRequest("test@nate.com", "test", "nickname");

        Member member = Member.builder()
                .email("test@nate.com")
                .password("test")
                .username("nickname")
                .loginType(LoginType.REGULAR)
                .status(MemberStatus.ACTIVE)
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);

        // when
        MemberResponse memberResponse = memberService.signUp(signupRequest);

        // then
        Assertions.assertThat(memberResponse).isNotNull();
        Assertions.assertThat(memberResponse.getEmail()).isEqualTo("test@nate.com");
        Assertions.assertThat(memberResponse.getUsername()).isEqualTo("nickname");
        Assertions.assertThat(memberResponse.getLoginType()).isEqualTo(LoginType.REGULAR);
        Assertions.assertThat(memberResponse.getStatus()).isEqualTo(MemberStatus.ACTIVE);

        verify(memberRepository, times(1)).existsByEmail(signupRequest.getEmail());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 시 이메일 중복 예외 발생")
    void duplicateEmailSignUpFail() {

        // given
        when(memberRepository.existsByEmail(anyString())).thenReturn(true);

        SignupRequest signupRequest = new SignupRequest("test@nate.com", "test", "nickname");

        // when & then
        Assertions.assertThatThrownBy(() ->
                        memberService.signUp(signupRequest)).isInstanceOf(MemberHandler.class)
                .hasFieldOrPropertyWithValue("code", ErrorStatus.EMAIL_DUPLICATE);

        verify(memberRepository, times(1)).existsByEmail(signupRequest.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("refresh token 있는 상태에서 로그인 성공")
    void loginSuccessExistsRefreshToken() {

        // given
        LoginRequest loginRequest = new LoginRequest("test@nate.com", "test");

        Member member = Member.builder()
                .email("test@nate.com")
                .password("test")
                .username("nickname")
                .loginType(LoginType.REGULAR)
                .status(MemberStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-token")
                .member(member)
                .build();

        when(memberRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(member));
        when(refreshTokenRepository.findByMemberId(member.getId())).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.createAccessToken(member, refreshToken.getId())).thenReturn("test-token");

        // when
        LoginResponse loginResponse = memberService.login(loginRequest);

        // then
        Assertions.assertThat(loginResponse).isNotNull();
        Assertions.assertThat(loginResponse.getId()).isEqualTo(1L);
        Assertions.assertThat(loginResponse.getEmail()).isEqualTo("test@nate.com");
        Assertions.assertThat(loginResponse.getUsername()).isEqualTo("nickname");
        Assertions.assertThat(loginResponse.getAccessToken()).isEqualTo("test-token");

        verify(refreshTokenRepository, times(1)).findByMemberId(member.getId());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        verify(jwtTokenProvider, times(1)).createAccessToken(member, refreshToken.getId());
        verify(memberRepository, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    @DisplayName("refreshToken 없는 상태에서 로그인 성공")
    void loginSuccessNotExistsRefreshToken() {

        // given
        LoginRequest loginRequest = new LoginRequest("test@nate.com", "test");

        Member member = Member.builder()
                .email("test@nate.com")
                .password("test")
                .username("nickname")
                .loginType(LoginType.REGULAR)
                .status(MemberStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-token")
                .member(member)
                .build();
        ReflectionTestUtils.setField(refreshToken, "id", 1L);

        when(jwtTokenProvider.createRefreshToken(member)).thenReturn("test-token");
        when(memberRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(member));
        when(refreshTokenRepository.findByMemberId(member.getId())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member, refreshToken.getId())).thenReturn("test-token");

        // when
        LoginResponse loginResponse = memberService.login(loginRequest);

        // then
        Assertions.assertThat(loginResponse).isNotNull();
        Assertions.assertThat(loginResponse.getId()).isEqualTo(1L);
        Assertions.assertThat(loginResponse.getEmail()).isEqualTo("test@nate.com");
        Assertions.assertThat(loginResponse.getUsername()).isEqualTo("nickname");
        Assertions.assertThat(loginResponse.getAccessToken()).isEqualTo("test-token");

        verify(refreshTokenRepository, times(1)).findByMemberId(member.getId());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        verify(jwtTokenProvider, times(1)).createRefreshToken(member);
        verify(jwtTokenProvider, times(1)).createAccessToken(member, refreshToken.getId());
        verify(memberRepository, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    @DisplayName("유효하지 않은 이메일 로그인 실패")
    void notValidEmailLoginFail() {

        // given
        LoginRequest loginRequest = new LoginRequest("test@nate.com", "test");

        when(memberRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() ->
                        memberService.login(loginRequest)).isInstanceOf(MemberHandler.class)
                .hasFieldOrPropertyWithValue("code", ErrorStatus.LOGIN_NOT_VALID);

    }

    @Test
    @DisplayName("유효하지 않은 비밀번호 로그인 실패")
    void notValidPasswordLoginFail() {

        // given
        LoginRequest loginRequest = new LoginRequest("test@nate.com", "test");

        Member member = Member.builder()
                .email("test@nate.com")
                .password("test-fail")
                .username("nickname")
                .loginType(LoginType.REGULAR)
                .status(MemberStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        when(memberRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(member));

        // when & then
        Assertions.assertThatThrownBy(() ->
                memberService.login(loginRequest)).isInstanceOf(MemberHandler.class)
                .hasFieldOrPropertyWithValue("code", ErrorStatus.LOGIN_NOT_VALID);
    }
}
