package com.unicksbyte.inkspire.config;

import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.repository.UserRepository;
import com.unicksbyte.inkspire.service.AppUserDetailsService;
import com.unicksbyte.inkspire.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AppUserDetailsService appUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // Find or create user
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        UserEntity user = existingUser.orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setUserName(name);
            newUser.setPassword("{noop}dummy"); // <-- dummy
            newUser.setRole("USER");
            newUser.setEmailVerified(true); // Google verified email
            return userRepository.save(newUser);
        });

        // Get UserDetails using your existing service
        UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

        // Generate JWT using same util as /login
        String jwtToken = jwtUtils.generateToken(userDetails);

        // Redirect to frontend
        String redirectUrl = "https://inkspire-application.netlify.app/oauth-success" +
                "?token=" + jwtToken +
                "&publicId=" + user.getPublicId() +
                "&userName=" + URLEncoder.encode(user.getUserName(), StandardCharsets.UTF_8) +
                "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
