package com.project.userservice.security.jwt;

import com.project.userservice.security.dto.JwtAuthenticationDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import user.model.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtService {

    @Value("${JWT_SECRET:f9f4180a2ec3cbc2104f873b6cc91c67267770349c8232fa8c23a24b3f9b714542f9abc1}")
    private String jwtSecret;

    public JwtAuthenticationDto generateAuthToken(String email, Role role) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email, role));
        jwtDto.setRefreshToken(generateRefreshToken(email));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken, Role role) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email, role));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        try {
            return getAllClaimsFromToken(token).get("role", String.class);
        } catch (Exception e) {
            return Role.ROLE_USER.name();
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateJwtToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("Expired JwtException", expiredJwtException);
        } catch (UnsupportedJwtException unsupportedJwtException) {
            log.error("Unsupported JwtException", unsupportedJwtException);
        } catch (MalformedJwtException malformedJwtException) {
            log.error("Malformed JwtException", malformedJwtException);
        } catch (SecurityException securityException) {
            log.error("Security Exception", securityException);
        } catch (Exception ex) {
            log.error("Invalid token", ex);
        }

        return false;
    }

    private String generateJwtToken(String email, Role role) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException ex) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
