package com.api.springsecurityauthcors.domain.DTO.auth;

import com.api.springsecurityauthcors.domain.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class JWTResponse {
    private String accessToken;
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private Role role;
}