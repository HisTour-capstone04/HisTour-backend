package com.capstone.HisTour.domain.push_token.repository;

import com.capstone.HisTour.domain.push_token.domain.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByDeviceId(String deviceId);
}
