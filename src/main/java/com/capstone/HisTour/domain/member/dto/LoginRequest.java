package com.capstone.HisTour.domain.member.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
