package com.capstone.HisTour.domain.visited.service;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.visited.domain.Visited;
import com.capstone.HisTour.domain.visited.dto.VisitedListResponse;
import com.capstone.HisTour.domain.visited.dto.VisitedRequest;
import com.capstone.HisTour.domain.visited.dto.VisitedResponse;
import com.capstone.HisTour.domain.visited.repostiory.VisitedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitedService {

    private final VisitedRepository visitedRepository;
    private final MemberRepository memberRepository;
    private final HeritageRepository heritageRepository;
    private final HeritageService heritageService;

    public VisitedResponse addVisited(Long memberId, VisitedRequest visitedRequest) {

        // 멤버 유효성 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Heritage heritage = heritageRepository.findById(visitedRequest.getHeritageId())
                .orElseThrow(() -> new IllegalArgumentException("Heritage not found"));

        Visited visited = new Visited(member, heritage);
        visited.setVisitedAt(LocalDateTime.now());

        return VisitedResponse.from(visitedRepository.save(visited));
    }

    public VisitedListResponse getVisitedList(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // member로 visited 조회
        List<Visited> visitedList = visitedRepository.findAllByMemberId(member.getId());

        // List<HeritageResponse>로 변환
        List<HeritageResponse> visitedHeritageList = visitedList.stream()
                .map(visited -> heritageService.getHeritageById(visited.getHeritage().getId()))
                .toList();

        return VisitedListResponse.from(visitedHeritageList);
    }

}
