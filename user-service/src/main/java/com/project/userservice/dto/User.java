package com.project.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User{

        @Null
        Long id;

        @NotNull
        String username;

        @NotNull
        String email;

        LocalDateTime registrationDate;
        UserStatus userStatus;
        String password;

}
