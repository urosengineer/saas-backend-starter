package com.urke.saasbackendstarter.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Handshake interceptor for authenticating WebSocket connections with JWT.
 */
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = null;

        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            for (String pair : query.split("&")) {
                if (pair.startsWith("token=")) {
                    token = pair.substring("token=".length());
                    break;
                }
            }
        }

        if (token == null) {
            List<String> headers = request.getHeaders().get("Authorization");
            if (headers != null && !headers.isEmpty()) {
                String header = headers.get(0);
                if (header.startsWith("Bearer ")) {
                    token = header.substring(7);
                }
            }
        }

        if (token != null) {
            try {
                String email = jwtTokenProvider.getEmailFromToken(token);
                attributes.put("email", email);
                return true;
            } catch (Exception e) {
                // Invalid token
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        // No token found
        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, @Nullable Exception exception
    ) {
        // No-op
    }
}