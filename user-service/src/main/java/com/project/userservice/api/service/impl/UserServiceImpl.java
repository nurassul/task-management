package com.project.userservice.api.service.impl;

import com.project.userservice.repository.UserRepository;
import com.project.userservice.repository.entity.UserEntity;
import com.project.userservice.utils.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.model.User;
import user.model.UserStatus;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public User findUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        return mapper.toDomainEntity(user);
    }

    public boolean checkExistingUser(Long id) {
        return userRepository.existsById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    public User createUser(User userToCreate) {
        var userEntityToSave = mapper.toEntity(userToCreate);

        var savedUser = userRepository.save(userEntityToSave);
        return mapper.toDomainEntity(savedUser);
    }

    public User updateUser(Long id, User userToUpdate) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        if (user.getUserStatus().equals(UserStatus.BANNED)) {
            throw new IllegalStateException("Cannot modify user! status= " + user.getUserStatus());
        }

        if (user.getUserStatus().equals(UserStatus.DELETED)) {
            throw new IllegalStateException("Cannot modify user! status= " + user.getUserStatus());
        }

        user.setUsername(userToUpdate.getUsername());
        user.setEmail(userToUpdate.getEmail());

        var updatedUser = userRepository.save(user);
        return mapper.toDomainEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        if (user.getUserStatus().equals(UserStatus.DELETED)) {
            throw new IllegalStateException("Cannot delete user! status= " + user.getUserStatus());
        }

        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Transactional
    public void banUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id= " + id + ", not found!"));

        if (user.getUserStatus().equals(UserStatus.BANNED)) {
            throw new IllegalStateException("Cannot ban user! status= " + user.getUserStatus());
        }

        if (user.getUserStatus().equals(UserStatus.DELETED)) {
            throw new IllegalStateException("Cannot ban user! status= " + user.getUserStatus());
        }

        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return mapper.toDomainEntity(userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found")));
    }
}
