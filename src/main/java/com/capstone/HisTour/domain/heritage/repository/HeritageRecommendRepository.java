package com.capstone.HisTour.domain.heritage.repository;

import com.capstone.HisTour.domain.heritage.domain.HeritageRecommend;
import com.capstone.HisTour.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeritageRecommendRepository extends JpaRepository<HeritageRecommend, Long> {
    List<HeritageRecommend> findAllByMember(Member member);
}
