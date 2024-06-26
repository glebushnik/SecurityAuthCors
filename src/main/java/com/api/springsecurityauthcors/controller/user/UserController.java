package com.api.springsecurityauthcors.controller.user;

import com.api.springsecurityauthcors.domain.DTO.user.UserResponseDTO;
import com.api.springsecurityauthcors.exception.user.UserNotFoundByIdException;
import com.api.springsecurityauthcors.security.JwtService;
import com.api.springsecurityauthcors.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;


    @GetMapping("/{userId}")
    @Operation(
            summary = "Retrieve User by Id",
            description = "Get a User object by specifying its id.",
            tags = { "users", "get" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = UserResponseDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (UserNotFoundByIdException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/current")
    @Operation(
            summary = "Get Current User Info",
            description = "Retrieve information about the currently logged-in user.",
            tags = { "users", "get", "auth" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = UserResponseDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })
    })
    public ResponseEntity<?> getCurrentUserInfo(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            String usernameFromAccess = jwtService.extractUserName(token);
            return ResponseEntity.ok().body(userService.getCurrentUser(usernameFromAccess));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
