package com.bancom.app.demo.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenConfig {
    
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    
    @Value("${jwt.secret.key}")
    private String secretKeyString;
    
    @Value("${jwt.expiration.time}")
    private Long expirationTime;
    
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }
    
    public Long getExpirationTime() {
        return expirationTime;
    }
}
