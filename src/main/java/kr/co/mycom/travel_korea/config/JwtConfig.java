package kr.co.mycom.travel_korea.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtConfig {

    private final byte[] secretKeyBytes;
    private final long accessExpiration;
    private final long refreshExpiration;
    public record TokenResponse(String accessToken, String refreshToken) {}
    public JwtConfig(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.secretKeyBytes = secret.getBytes();
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // 1. Access Token과 Refresh Token 동시 발급
    public TokenResponse createTokenPair(String username) throws JOSEException {
        JWSSigner signer = new MACSigner(secretKeyBytes);
        Date now = new Date();

        // Access Token 생성 (사용자 식별 정보 포함)
        JWTClaimsSet accessClaims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("type", "ACCESS")
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + accessExpiration))
                .build();
        SignedJWT accessToken = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), accessClaims);
        accessToken.sign(signer);

        // Refresh Token 생성 (보안을 위해 최소한의 식별 정보인 무작위 UUID만 포함)
        JWTClaimsSet refreshClaims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .claim("type", "REFRESH")
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + refreshExpiration))
                .build();
        SignedJWT refreshToken = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), refreshClaims);
        refreshToken.sign(signer);

        return new TokenResponse(accessToken.serialize(), refreshToken.serialize());
    }

    // 2. Access Token 검증 및 Username 추출 (API 요청 처리용)
    public String validateAccessToken(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secretKeyBytes);

        if (!signedJWT.verify(verifier)) {
            throw new IllegalArgumentException("토큰 서명이 올바르지 않습니다.");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        if (claims.getExpirationTime().before(new Date())) {
            throw new IllegalArgumentException("만료된 Access Token입니다.");
        }

        if (!"ACCESS".equals(claims.getStringClaim("type"))) {
            throw new IllegalArgumentException("올바른 Access Token 타입이 아닙니다.");
        }

        return claims.getSubject();
    }

    // 3. Refresh Token 검증 (토큰 재발급 요청 처리용)
    public void validateRefreshToken(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secretKeyBytes);

        if (!signedJWT.verify(verifier)) {
            throw new IllegalArgumentException("토큰 서명이 올바르지 않습니다.");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        if (claims.getExpirationTime().before(new Date())) {
            throw new IllegalArgumentException("만료된 Refresh Token입니다. 다시 로그인하세요.");
        }

        if (!"REFRESH".equals(claims.getStringClaim("type"))) {
            throw new IllegalArgumentException("올바른 Refresh Token 타입이 아닙니다.");
        }
    }
    //4. Token 삭제
    public void DeleteToken() throws Exception {



    }
}
