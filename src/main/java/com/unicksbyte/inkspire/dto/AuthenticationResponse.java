package com.unicksbyte.inkspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {

    private String email;

    private String userName;

    private String token;

    private String publicId;

    private boolean emailVerified;

    private String role;
}
