package com.capstone.HisTour.domain.member.domain;

import com.capstone.HisTour.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Builder
    public Member(String email, String password, String username, LoginType loginType, MemberStatus status) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.loginType = loginType;
        this.status = status;
    }

    public void deactivateUser() {
        this.status = MemberStatus.INACTIVE;
    }

    public void activateUser() {
        this.status = MemberStatus.ACTIVE;
    }
}
