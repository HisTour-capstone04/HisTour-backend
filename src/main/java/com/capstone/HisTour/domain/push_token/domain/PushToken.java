package com.capstone.HisTour.domain.push_token.domain;

import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "push_token")
@Getter
public class PushToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    @Setter
    private Member member;

    @Column(name = "push_token")
    private String pushToken;

    @Column(name = "device_id")
    private String deviceId;

    @Builder
    public PushToken(Member member, String pushToken) {
        this.member = member;
        this.pushToken = pushToken;
    }
}
