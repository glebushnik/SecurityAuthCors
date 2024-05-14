package com.api.springsecurityauthcors.service.user;

import com.api.springsecurityauthcors.domain.DTO.user.UserResponseDTO;
import com.api.springsecurityauthcors.domain.entity.UserEntity;
import com.api.springsecurityauthcors.exception.user.UserNotFoundByIdException;

import java.util.List;

public interface UserService {
    public List<UserResponseDTO> getAllUsers();
    public UserResponseDTO getUserById(Long userId) throws UserNotFoundByIdException;
    public UserResponseDTO getCurrentUser(String userNameFromAccess);
}
