package com.api.springsecurityauthcors.controller.auth;

import com.api.springsecurityauthcors.domain.DTO.auth.*;
import com.api.springsecurityauthcors.domain.entity.RefreshToken;
import com.api.springsecurityauthcors.security.JwtService;
import com.api.springsecurityauthcors.service.auth.AuthenticationService;
import com.api.springsecurityauthcors.service.auth.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(
            summary = "Register User",
            description = "Register a new user. The request body should be a RegisterRequest object with name, email and password. The response is a JWTResponse object with role, access token and refresh token.",
            tags = { "users", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = JWTResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        try {
            var accessToken = authenticationService.register(request).getToken();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());
            return ResponseEntity.ok().body(
                    JWTResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken.getToken())
                            .role(refreshToken.getUser().getRole())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate User",
            description = "Authenticate a user. The request body should be an AuthenticationRequest object with email and password. The response is a JWTResponse object with role, access token and refresh token.",
            tags = { "users", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = JWTResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());
        return ResponseEntity.ok().body(
                JWTResponse.builder()
                        .accessToken(authenticationService.authenticate(request).getToken())
                        .refreshToken(refreshToken.getToken())
                        .role(refreshToken.getUser().getRole())
                        .build()
        );
    }

    @PostMapping("/refreshtoken")
    @Operation(
            summary = "Refresh Token",
            description = "Refresh a user's token. The request body should be a RefreshTokenRequest object with refresh token. The response is a JWTResponse object with role, new access token and refresh token.",
            tags = { "users", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = JWTResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getToken()).get();
            String userName = refreshToken.getUser().getEmail();
            return ResponseEntity.ok().body(
                    JWTResponse.builder()
                            .refreshToken(refreshTokenService.updateRefreshToken(refreshToken.getToken()).getToken())
                            .accessToken(jwtService.generateToken(userName))
                            .role(refreshToken.getUser().getRole())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change Password",
            description = "Change the password of a user by their ID. The request body should be a ChangePassRequest object with user ID and new password. Only administrators are allowed to perform this action.",
            tags = { "users", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    public ResponseEntity<?> changePasswordById(
            @RequestBody ChangePasswordRequest changePassrequest,
            HttpServletRequest request
    ) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            String usernameFromAccess = jwtService.extractUserName(token);
            return ResponseEntity.ok().body(authenticationService.changePassword(changePassrequest, usernameFromAccess));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
