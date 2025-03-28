package com.capstone.HisTour.domain.member.service;

import com.capstone.HisTour.domain.member.domain.LoginType;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.domain.MemberStatus;
import com.capstone.HisTour.domain.member.dto.SignupRequest;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 로직
    public void signUp(SignupRequest signupRequest) {

        // signupRequest validation 확인
        Member member = registerValid(signupRequest);

        // member DB에 저장
        memberRepository.save(member);
    }

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
}
