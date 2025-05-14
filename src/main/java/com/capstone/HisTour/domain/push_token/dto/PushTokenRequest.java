package com.capstone.HisTour.domain.push_token.dto;

import lombok.Getter;

@Getter
public class PushTokenRequest {
    private String pushToken;
    private String deviceId;
}
