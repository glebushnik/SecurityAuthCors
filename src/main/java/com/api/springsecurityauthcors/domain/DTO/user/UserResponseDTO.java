package com.api.springsecurityauthcors.domain.DTO.user;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
}
