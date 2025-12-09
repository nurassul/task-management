package com.project.userservice.service;

import com.project.userservice.dto.JwtAuthenticationDto;
import com.project.userservice.dto.RefreshTokenDto;
import com.project.userservice.dto.UserCredentialsDto;
import org.apache.tomcat.websocket.AuthenticationException;

public interface UserService {

    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException;
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;

}
