package com.enginai.backend.authn.handler;

import com.enginai.backend.authn.service.AuthnService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthnService authnService;

    @Value("${cors.allowed-origin}")
    private String frontendOrigin;

    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String googleId = oAuth2User.getAttribute("sub");
        String email    = oAuth2User.getAttribute("email");
        String name     = oAuth2User.getAttribute("name");
        String picture  = oAuth2User.getAttribute("picture");

        AuthnService.TokenPair tokens = authnService.loginWithGoogle(googleId, email, name, picture);

        // RT → httpOnly cookie
        Cookie rtCookie = new Cookie("refresh_token", tokens.refreshToken());
        rtCookie.setHttpOnly(true);
        rtCookie.setPath("/api/authn/refresh");
        rtCookie.setMaxAge((int) (refreshTokenExpiryMs / 1000));
        rtCookie.setSecure(false); // set true in prod (HTTPS)
        response.addCookie(rtCookie);

        // AT → query param so React can pick it up on the callback page
        String redirectUrl = frontendOrigin + "/auth/callback?access_token=" + tokens.accessToken();
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
