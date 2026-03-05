package com.project.userservice.api.service;

import com.project.userservice.security.dto.JwtAuthenticationDto;
import com.project.userservice.security.dto.RefreshTokenDto;
import com.project.userservice.security.dto.UserCredentialsDto;

public interface UserService {

    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto);

    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);
}
