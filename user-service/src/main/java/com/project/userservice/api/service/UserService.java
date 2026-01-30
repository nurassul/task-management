package com.project.userservice.api.service;

import com.project.userservice.security.dto.JwtAuthenticationDto;
import com.project.userservice.security.dto.RefreshTokenDto;
import com.project.userservice.security.dto.UserCredentialsDto;
import org.apache.tomcat.websocket.AuthenticationException;

public interface UserService {

    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException;
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;

}
