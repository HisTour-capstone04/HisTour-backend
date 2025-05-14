package com.capstone.HisTour.domain.alarm.dto;

import com.capstone.HisTour.domain.alarm.domain.Alarm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlarmResponse {
    private String title;
    private String body;
    private LocalDateTime createdAt;

    public static AlarmResponse from(Alarm alarm) {
        return AlarmResponse.builder()
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}
