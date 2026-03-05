package com.project.userservice.api.controller;

import com.project.userservice.api.service.UserService;
import com.project.userservice.security.dto.JwtAuthenticationDto;
import com.project.userservice.security.dto.RefreshTokenDto;
import com.project.userservice.security.dto.UserCredentialsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> signIn(@RequestBody UserCredentialsDto userCredentialsDto) {
        JwtAuthenticationDto jwtAuthenticationDto = userService.signIn(userCredentialsDto);
        return ResponseEntity.ok(jwtAuthenticationDto);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        return userService.refreshToken(refreshTokenDto);
    }
}
