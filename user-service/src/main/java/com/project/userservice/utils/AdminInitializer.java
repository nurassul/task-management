package com.project.userservice.utils;


import com.project.userservice.repository.UserRepository;
import com.project.userservice.repository.entity.UserEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import user.model.Role;


@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByEmail("admin123").isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(encoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);

            log.info("Account admin successfully created!");
        } else {
            log.info("Account admin already exists!");
        }

    }
}
