package com.capstone.HisTour.domain.member.dto;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String username;
}
