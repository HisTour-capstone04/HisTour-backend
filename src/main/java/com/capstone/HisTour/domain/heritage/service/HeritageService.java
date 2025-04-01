package com.capstone.HisTour.domain.heritage.service;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
