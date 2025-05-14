package com.capstone.HisTour.domain.alarm.dto;

import lombok.Getter;

@Getter
public class AlarmRequest {
    private Double latitude;
    private Double longitude;
    private String deviceId;
    private Double range;
}
