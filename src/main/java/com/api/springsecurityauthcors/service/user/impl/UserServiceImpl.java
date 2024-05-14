package com.api.springsecurityauthcors.service.user.impl;

import com.api.springsecurityauthcors.domain.DTO.user.UserResponseDTO;
import com.api.springsecurityauthcors.domain.entity.UserEntity;
import com.api.springsecurityauthcors.domain.mapper.user.UserResponseMapper;
import com.api.springsecurityauthcors.exception.user.UserNotFoundByIdException;
import com.api.springsecurityauthcors.repo.user.UserRepo;
import com.api.springsecurityauthcors.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UserResponseMapper userResponseMapper;
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userResponseMapper.toDto(userRepo.findAll());
    }
    @Override
    public UserResponseDTO getCurrentUser(String userNameFromAccess) {
        return userResponseMapper.toDto(userRepo.findByEmail(userNameFromAccess).get());
    }
    @Override
    public UserResponseDTO getUserById(Long userId) throws UserNotFoundByIdException {
        return userResponseMapper.toDto(userRepo.findById(userId).orElseThrow(()
                ->new UserNotFoundByIdException(String.format("Пользователь с id : %d не найден",userId))));

    }
}

