package com.api.springsecurityauthcors.exception.refresh;

public class RefreshTokenNotFoundByTokenException extends Exception{
    public RefreshTokenNotFoundByTokenException(String message) {
        super(message);
    }
}
