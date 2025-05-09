package com.capstone.HisTour.domain.push_token.repository;

import com.capstone.HisTour.domain.push_token.domain.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
}
