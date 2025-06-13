package com.urke.saasbackendstarter.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for tracking failed login attempts and temporarily blocking accounts after too many failures.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntil = new ConcurrentHashMap<>();

    /**
     * Should be called on successful login. Resets attempt counter and removes any block.
     */
    public void loginSucceeded(String email) {
        attempts.remove(email);
        blockedUntil.remove(email);
    }

    /**
     * Should be called on failed login. Increments attempt counter and applies block if threshold is reached.
     */
    public void loginFailed(String email) {
        int current = attempts.getOrDefault(email, 0);
        attempts.put(email, current + 1);
        if (attempts.get(email) >= MAX_ATTEMPTS) {
            blockedUntil.put(email, Instant.now().toEpochMilli() + BLOCK_DURATION_MS);
        }
    }

    /**
     * Checks if a user is currently blocked.
     */
    public boolean isBlocked(String email) {
        Long until = blockedUntil.get(email);
        if (until == null) return false;
        if (Instant.now().toEpochMilli() > until) {
            blockedUntil.remove(email);
            attempts.remove(email);
            return false;
        }
        return true;
    }

    /**
     * Returns the number of milliseconds left before the block is lifted.
     */
    public long getBlockTimeRemaining(String email) {
        Long until = blockedUntil.get(email);
        if (until == null) return 0;
        long diff = until - Instant.now().toEpochMilli();
        return Math.max(diff, 0);
    }
}