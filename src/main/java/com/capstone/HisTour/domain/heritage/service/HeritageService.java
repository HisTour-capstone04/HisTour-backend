package com.capstone.HisTour.domain.heritage.service;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageNearbyResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeritageService {

    private final HeritageRepository heritageRepository;

    // 특정 유적지 조회
    public HeritageResponse getHeritageById(Long id) {

        // id를 통해서 heritage 조회
        Heritage heritage = heritageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id를 가진 Heritage가 존재하지 않습니다."));

        // HeritageResponse 반환
        return HeritageResponse.from(heritage);
    }

    // 근처 유적지 리스트 조회
    public HeritageNearbyResponse getHeritageNearby(double latitude, double longitude, double radius) {

        // 위도, 경도, radius를 사용하여 근처 유적지 조회
        List<Heritage> heritagesNearby = heritageRepository.findNearbyHeritages(latitude, longitude, radius);

        // HeritageNearbyResponse 반환
        return HeritageNearbyResponse.from(heritagesNearby);
    }

    // 유적지 이름으로 조회
    public List<HeritageResponse> searchHeritageByName(String name) {

        // heritage 이름으로 검색
        List<Heritage> heritages = heritageRepository.findByNameContainingIgnoreCase(name);

        // List<HeritageResponse> 반환
        return heritages.stream()
                .map(HeritageResponse::from)
                .toList();

    }
}
