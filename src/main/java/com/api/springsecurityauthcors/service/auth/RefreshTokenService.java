package com.api.springsecurityauthcors.service.auth;

import com.api.springsecurityauthcors.domain.entity.RefreshToken;
import com.api.springsecurityauthcors.exception.refresh.RefreshTokenNotFoundByTokenException;
import com.api.springsecurityauthcors.repo.refresh.RefreshTokenRepo;
import com.api.springsecurityauthcors.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    public RefreshToken createRefreshToken(String username) {
        if(userRepo.findByEmail(username).get().getRefreshToken()!=null) {
            return userRepo.findByEmail(username).get().getRefreshToken();
        }
        return refreshTokenRepo.save(
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .expiration(Instant.now().plusSeconds(1296000))
                        .user(userRepo.findByEmail(username).get())
                        .build()
        );
    }

    public RefreshToken updateRefreshToken(String token) throws RefreshTokenNotFoundByTokenException {
        var refreshToken = refreshTokenRepo.findRefreshTokenByToken(token).orElseThrow(()
                -> new RefreshTokenNotFoundByTokenException(String.format("RefreshToken с token : %s не найден.", token)));
        var user = refreshToken.getUser();
        var username = user.getEmail();
        user.setRefreshToken(null);
        refreshTokenRepo.delete(refreshToken);

        return refreshTokenRepo.save(
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .expiration(Instant.now().plusSeconds(1296000))
                        .user(userRepo.findByEmail(username).get())
                        .build()
        );
    }
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findRefreshTokenByToken(token);
    }

}
