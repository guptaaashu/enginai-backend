package com.enginai.backend.authn.controller;

import com.enginai.backend.authn.service.AuthnService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/authn")
@RequiredArgsConstructor
public class AuthnController {

    private final AuthnService authnService;

    /**
     * POST /api/authn/refresh
     * Reads RT from httpOnly cookie, issues new AT (+ rotates RT).
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refresh_token");
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No refresh token"));
        }

        try {
            AuthnService.TokenPair tokens = authnService.refresh(refreshToken);

            // Rotate RT cookie
            Cookie rtCookie = new Cookie("refresh_token", tokens.refreshToken());
            rtCookie.setHttpOnly(true);
            rtCookie.setPath("/api/authn/refresh");
            rtCookie.setMaxAge(7 * 24 * 60 * 60);
            rtCookie.setSecure(false); // true in prod
            response.addCookie(rtCookie);

            return ResponseEntity.ok(Map.of("accessToken", tokens.accessToken()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/authn/logout
     * Clears the RT cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie rtCookie = new Cookie("refresh_token", "");
        rtCookie.setHttpOnly(true);
        rtCookie.setPath("/api/authn/refresh");
        rtCookie.setMaxAge(0);
        response.addCookie(rtCookie);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
