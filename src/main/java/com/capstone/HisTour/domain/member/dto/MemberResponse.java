package com.capstone.HisTour.domain.member.dto;

import com.capstone.HisTour.domain.member.domain.LoginType;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.domain.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String email;
    private String username;
    private LoginType loginType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MemberStatus status;

    // Member -> MemberResponse 변환
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .loginType(member.getLoginType())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .status(member.getStatus())
                .build();
    }
}
