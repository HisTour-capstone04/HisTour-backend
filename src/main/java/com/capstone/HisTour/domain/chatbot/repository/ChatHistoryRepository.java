package com.capstone.HisTour.domain.chatbot.repository;

import com.capstone.HisTour.domain.chatbot.domain.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

}
