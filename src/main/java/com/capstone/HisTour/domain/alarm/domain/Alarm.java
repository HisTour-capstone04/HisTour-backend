package com.capstone.HisTour.domain.alarm.domain;

import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "alarm")
public class Alarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Builder
    public Alarm(Member member, String title, String body) {
        this.member = member;
        this.title = title;
        this.body = body;
    }
}
