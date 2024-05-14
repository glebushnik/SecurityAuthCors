package com.api.springsecurityauthcors.service.auth;

import com.api.springsecurityauthcors.domain.DTO.auth.*;
import com.api.springsecurityauthcors.domain.enums.Role;
import com.api.springsecurityauthcors.exception.user.UserNotFoundByIdException;
import com.api.springsecurityauthcors.repo.user.UserRepo;
import com.api.springsecurityauthcors.security.JwtService;
import com.api.springsecurityauthcors.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {

        var user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .patronymic(request.getPatronymic())
                .build();
        userRepo.save(user);
        var jwtToken = jwtService.generateToken(user.getEmail());
        return  AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();

        var jwtToken = jwtService.generateToken(user.getEmail());
        return  AuthenticationResponse.builder().token(jwtToken).build();
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request, String userNameFromAccess) throws UserNotFoundByIdException {
        var user = userRepo.findByEmail(userNameFromAccess).get();

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        userRepo.save(user);
        return ChangePasswordResponse.builder()
                .newPassword(request.getPassword())
                .build();
    }
}
