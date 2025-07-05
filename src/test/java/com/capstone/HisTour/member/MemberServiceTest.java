package com.capstone.HisTour.member;

import com.capstone.HisTour.domain.apiPayload.exception.GeneralException;
import com.capstone.HisTour.domain.apiPayload.exception.handler.MemberHandler;
import com.capstone.HisTour.domain.apiPayload.status.ErrorStatus;
import com.capstone.HisTour.domain.member.domain.LoginType;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.domain.MemberStatus;
import com.capstone.HisTour.domain.member.dto.MemberResponse;
import com.capstone.HisTour.domain.member.dto.SignupRequest;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

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
    void duplicateEmail() {

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
}
