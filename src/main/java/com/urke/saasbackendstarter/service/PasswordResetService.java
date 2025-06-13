package com.urke.saasbackendstarter.service;

public interface PasswordResetService {
    void createResetToken(String email);
    void resetPassword(String token, String newPassword);
}
