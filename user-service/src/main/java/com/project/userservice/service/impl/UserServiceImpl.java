package com.project.userservice.service.impl;


import com.project.userservice.dto.*;
import com.project.userservice.entity.UserEntity;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.jwt.JwtService;
import com.project.userservice.service.UserService;
import com.project.userservice.utils.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    // find user by id
    public User findUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        return mapper.toDomainEntity(user);

    }

    // find all users
    public List<User> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    // create user
    public User createUser(User userToCreate) {
        var userEntityToSave = mapper.toEntity(userToCreate);
        userEntityToSave.setPassword(passwordEncoder.encode(userEntityToSave.getPassword()));

        var savedUser = userRepository.save(userEntityToSave);

        return mapper.toDomainEntity(savedUser);
    }

    // update user
    public User updateUser(Long id, User userToUpdate) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        if(user.getUserStatus().equals(UserStatus.BANNED)){
            throw new IllegalStateException("Cannot modify user! status= " + user.getUserStatus());
        }

        if(user.getUserStatus().equals(UserStatus.DELETED)){
            throw new IllegalStateException("Cannot modify user! status= " + user.getUserStatus());
        }

        user.setUsername(userToUpdate.getUsername());
        user.setEmail(userToUpdate.getEmail());


        var updatedTask = userRepository.save(user);
        return mapper.toDomainEntity(updatedTask);
    }

    // change status to DELETED
    @Transactional
    public void deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        if(user.getUserStatus().equals(UserStatus.DELETED)){
            throw new IllegalStateException("Cannot delete user! status= " + user.getUserStatus());
        }

        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);

    }

    // change status to BANNED
    @Transactional
    public void banUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        if(user.getUserStatus().equals(UserStatus.BANNED)){
            throw new IllegalStateException("Cannot ban user! status= " + user.getUserStatus());
        }

        if(user.getUserStatus().equals(UserStatus.DELETED)){
            throw new IllegalStateException("Cannot ban user! status= " + user.getUserStatus());
        }

        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    public User getUserByEmail(String email) throws ChangeSetPersister.NotFoundException {
        return mapper.toDomainEntity(userRepository.findByEmail(email)
                .orElseThrow(ChangeSetPersister.NotFoundException::new));
    }


    @Override
    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        UserEntity user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)){
            UserEntity user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }

        throw new AuthenticationException("Invalid refresh token");
    }

    private UserEntity findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userCredentialsDto.getEmail());
        if(optionalUser.isPresent()){
            UserEntity userEntity= optionalUser.get();

            if(passwordEncoder.matches(userCredentialsDto.getPassword(), userEntity.getPassword())){
                return userEntity;
            }
        }

        throw new AuthenticationException("Email or password not correct");
    }

    private UserEntity findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(() -> new Exception(String.format("User with email %s not found", email)));
    }
}
