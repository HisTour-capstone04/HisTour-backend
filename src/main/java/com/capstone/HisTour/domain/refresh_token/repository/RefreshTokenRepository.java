package com.capstone.HisTour.domain.refresh_token.repository;

import com.capstone.HisTour.domain.refresh_token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long memberId);
}
