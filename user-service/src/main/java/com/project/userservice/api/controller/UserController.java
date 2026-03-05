package com.project.userservice.api.controller;

import com.project.userservice.api.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.model.User;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        log.info("Called findUserById(): id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserById(id));
    }

    @GetMapping("/private/{id}")
    public ResponseEntity<User> getPrivateUser(@PathVariable("id") Long id) {
        log.info("Called getPrivate(): id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserById(id));
    }

    @GetMapping("/check-existing/{id}")
    public ResponseEntity<Boolean> checkUserExisting(@PathVariable("id") Long id) {
        log.info("Called checkUserExisting(): id={}", id);
        return ResponseEntity.ok(userService.checkExistingUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Called findAllUsers()");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAllUsers());
    }

    @PostMapping("/registration")
    public ResponseEntity<User> createUser(@RequestBody @Valid User userToCreate) {
        log.info("Called createUser()");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userToCreate));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Called deleteUser() id={}", id);

        userService.deleteUser(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/banUser")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        log.info("Called banUser(): id={}", id);

        userService.banUser(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) throws ChangeSetPersister.NotFoundException {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") Long id,
            @RequestBody User userToUpdate
    ) {
        log.info("Called updateUser(): id={}", id);

        var updatedUser = userService.updateUser(id, userToUpdate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }
}
