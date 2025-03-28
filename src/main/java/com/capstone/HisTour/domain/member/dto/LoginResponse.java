package com.capstone.HisTour.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long id;
    private String email;
    private String username;
    private String accessToken;
}
