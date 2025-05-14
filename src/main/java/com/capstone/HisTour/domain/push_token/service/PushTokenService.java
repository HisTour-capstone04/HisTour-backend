package com.capstone.HisTour.domain.push_token.service;

import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.push_token.domain.PushToken;
import com.capstone.HisTour.domain.push_token.dto.PushTokenRequest;
import com.capstone.HisTour.domain.push_token.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushTokenService {

    private final PushTokenRepository pushTokenRepository;
    private final MemberRepository memberRepository;

    public void saveOrUpdatePushToken(PushTokenRequest pushTokenRequest, Long memberId) {

        Member member = null;
        if (memberId != null) {
            memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member id: " + memberId));
        }

        pushTokenRepository.save(PushToken.builder()
                .member(member)
                .pushToken(pushTokenRequest.getPushToken())
                .build());
    }
}
