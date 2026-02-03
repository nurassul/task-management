package com.project.notificationservice.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import user.model.User;

@FeignClient(name = "user-api", url = "http://user-api:8080")
public interface UserClient {

    @GetMapping("/users/private/{id}")
    User getUserById(@PathVariable("id") Long id);

}
