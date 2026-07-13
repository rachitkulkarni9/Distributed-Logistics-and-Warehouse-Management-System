package com.logistics.common.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserSummary user;

    @Data
    @Builder
    public static class UserSummary {
        private String id;
        private String email;
        private String fullName;
        private String role;
    }
}
