package com.capstone.HisTour.domain.alarm.service;

import com.capstone.HisTour.domain.alarm.domain.Alarm;
import com.capstone.HisTour.domain.alarm.dto.AlarmRequest;
import com.capstone.HisTour.domain.alarm.repository.AlarmRepository;
import com.capstone.HisTour.domain.heritage.dto.HeritageNearbyResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.push_token.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PushTokenRepository pushTokenRepository;
    private final HeritageService heritageService;
    private final MemberRepository memberRepository;

    public void sendPushNotification(Long memberId, AlarmRequest alarmRequest) {
        String url = "https://exp.host/--/api/v2/push/send";

        Member member = memberRepository.findById(memberId)
                .orElse(null);

        // find nearby heritages using lat, long, range
        HeritageNearbyResponse heritagesNearby = heritageService.getHeritageNearby(
                alarmRequest.getLatitude(), alarmRequest.getLongitude(), alarmRequest.getRange());

        Map<String, Object> message = new HashMap<>();
        message.put("to", pushTokenRepository.findByDeviceId(alarmRequest.getDeviceId())
                .orElseThrow(() -> new RuntimeException("No push token found"))
                .getPushToken());
        message.put("sound", "default");

        String title = "근처에 " + heritagesNearby.getCount() + "개의 유적지가 있습니다.";
        message.put("title", title);

        String body = heritagesNearby.getHeritages().stream().map(HeritageResponse::getName).toString();
        message.put("body", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // 알람 DB 저장
        if (member != null) {
            alarmRepository.save(Alarm.builder()
                    .member(member)
                    .title(title)
                    .body(body)
                    .build());
        }

        log.info("Push Response: {}", response.getBody());
    }

}
