package com.capstone.HisTour.domain.refresh_token.domain;

import com.capstone.HisTour.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public RefreshToken(String token, Member member) {
        this.token = token;
        this.member = member;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
