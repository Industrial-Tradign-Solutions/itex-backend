package com.itradingsolutions.itex.config.security.jwt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDetailDTO;
import com.itradingsolutions.itex.config.security.SimpleGrantedAuthorityMixin;
import com.itradingsolutions.itex.config.security.jwt.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${itex.jwt.secret-key}")
    private String secretKey;
    @Value("${itex.jwt.application-name}")
    private String applicationName;
    @Value("${itex.jwt.expiration-token-hours}")
    private long expirationTokenHours;

    @Override
    public String getUserApp(String token) {
        return getClaimsToken(token, Claims::getSubject);
    }
    @Override
    public String createToken(Authentication auth) throws IOException {

        return Jwts
                .builder()
                .claim("authorities", new ObjectMapper().writeValueAsString(auth.getAuthorities()))
                .claim("sub", ((UserDetailDTO) auth.getPrincipal()).getUsername())
                .claim("iat", new Date(System.currentTimeMillis()))
                .claim("exp", new Date(getExpirationTokenMillis()))
                .claim("applicationId", applicationName)
                .signWith(getSigningKey())
                .compact();
    }
    @Override
    public long getExpirationTokenMillis() {
        Instant expirationToken = Instant.now().plus(expirationTokenHours, ChronoUnit.HOURS);
        return expirationToken.toEpochMilli();
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return getClaimsToken(token, Claims::getExpiration);
    }
    @Override
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
    @Override
    public Collection<SimpleGrantedAuthority> getRoles(String token) throws IOException {
        Object roles = getClaimsToken(token, claims -> claims.get("authorities"));
        return Arrays.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
                .readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
    }

    private <T> T getClaimsToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(resolve(token))
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String resolve(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.replace(TOKEN_PREFIX, "");
        }
        return null;
    }
}
