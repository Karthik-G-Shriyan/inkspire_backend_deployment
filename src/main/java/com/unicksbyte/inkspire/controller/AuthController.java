package com.unicksbyte.inkspire.controller;

import com.unicksbyte.inkspire.dto.AuthenticationRequest;
import com.unicksbyte.inkspire.dto.AuthenticationResponse;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.service.AppUserDetailsService;
import com.unicksbyte.inkspire.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final AppUserDetailsService userDetailsService;

    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());



        boolean isUser = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        if (!isUser) {
            throw new RuntimeException("Access denied: not an valid user.");
        }

        UserEntity userEntity = userDetailsService.findByEmail(request.getEmail());
        boolean emailVerified = userEntity.isEmailVerified();

        final String jwtToken = jwtUtils.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .email(request.getEmail())
                .token(jwtToken)
                .userName(userEntity.getUserName())
                .publicId(userEntity.getPublicId())
                .role(userEntity.getRole())
                .emailVerified(userEntity.isEmailVerified())
                .build();

    }
}
