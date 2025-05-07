package com.capstone.HisTour.domain.chatbot.domain;

import com.capstone.HisTour.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String question;

    private String answer;

    private LocalDateTime timestamp;

    @Builder
    public ChatHistory(Member member, String question, String answer, LocalDateTime timestamp) {
        this.member = member;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }
}
