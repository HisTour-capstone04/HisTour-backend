package com.capstone.HisTour.global.auth.jwt;

import com.capstone.HisTour.domain.member.domain.Member;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    // access token 만료 기한 1시간
    private final long accessExpiration = 1000 * 60 * 60;

    // refresh token 만료 기한 7일
    private final long refreshExpiration =  7 * 1000 * 60 * 60 * 24;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createAccessToken(Member member) {
        return Jwts.builder()
                .claim("memberId", member.getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Member member) {
        return Jwts.builder()
                .claim("memberId", member.getId())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (NullPointerException | JwtException e) {
            return false;
        }
    }

}
