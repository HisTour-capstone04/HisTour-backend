package com.capstone.HisTour.domain.bookmark.domain;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmark")
@NoArgsConstructor
@Getter
public class Bookmark extends BaseTimeEntity {

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

    @Column(name = "created_at", nullable = false)
    @Setter
    private LocalDateTime createdAt;

    public Bookmark(Member member, Heritage heritage) {
        this.member = member;
        this.heritage = heritage;
    }
}
