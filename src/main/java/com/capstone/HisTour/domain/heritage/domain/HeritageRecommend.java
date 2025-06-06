package com.capstone.HisTour.domain.heritage.domain;

import com.capstone.HisTour.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "heritage_recommend")
@NoArgsConstructor
@Getter
public class HeritageRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heritage_id", nullable = false)
    private Heritage heritage;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Builder
    public HeritageRecommend(Member member, Heritage heritage, String reason) {
        this.member = member;
        this.heritage = heritage;
        this.reason = reason;
    }
}
