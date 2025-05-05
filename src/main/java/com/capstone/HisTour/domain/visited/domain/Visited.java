package com.capstone.HisTour.domain.visited.domain;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "visited")
@NoArgsConstructor
@Getter
public class Visited extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heritage_id")
    private Heritage heritage;

    @Column(name = "visited_at", nullable = false)
    @Setter
    private LocalDateTime visitedAt;

    public Visited(Member member, Heritage heritage) {
        this.member = member;
        this.heritage = heritage;
    }
}
