package com.enginai.backend.authn.service;

import com.enginai.backend.authn.entity.User;
import com.enginai.backend.authn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthnService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    /** Find or create a user from Google OAuth2 attributes, then issue tokens. */
    public TokenPair loginWithGoogle(String googleId, String email, String name, String picture) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.save(new User(googleId, email, name, picture)));

        return issueTokens(user.getId());
    }

    /** Issue a fresh AT using a valid RT. */
    public TokenPair refresh(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        Long userId = jwtService.extractUserId(refreshToken);
        return issueTokens(userId);
    }

    private TokenPair issueTokens(Long userId) {
        return new TokenPair(
                jwtService.generateAccessToken(userId),
                jwtService.generateRefreshToken(userId)
        );
    }

    public record TokenPair(String accessToken, String refreshToken) {}
}
