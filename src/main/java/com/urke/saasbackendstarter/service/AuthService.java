package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.dto.auth.LoginRequest;
import com.urke.saasbackendstarter.dto.auth.LoginResponse;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenRequest;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void logout(String email);
}