package com.capstone.HisTour.domain.member.dto;

import com.capstone.HisTour.domain.member.domain.Member;
import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String username;
}
