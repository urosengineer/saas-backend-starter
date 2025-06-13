package com.urke.saasbackendstarter.security;

import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility for generating and validating JWT tokens.
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Generates a JWT token for the given user.
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getKey())
                .compact();
    }

    /**
     * Extracts the email (subject) from the token.
     */
    public String getEmailFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("JWT expired", ex);
        } catch (JwtException ex) {
            throw new RuntimeException("JWT invalid", ex);
        }
    }

    /**
     * Validates the token against the user details.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getEmailFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException ex) {
            return false;
        }
    }

    /**
     * Checks if the token is expired.
     */
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }

    /**
     * Builds an Authentication object from the token and user details.
     */
    public Authentication getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}