package com.project.taskservice.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import user.model.User;

import java.util.Optional;

@FeignClient(name = "user-api", url = "http://user-api:8080")
public interface UserClient {

    @GetMapping("/users/check-existing/{id}")
    Optional<Boolean> checkUserExisting(@PathVariable("id") Long id);
}
