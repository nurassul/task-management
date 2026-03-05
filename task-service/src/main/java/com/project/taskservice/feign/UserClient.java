package com.project.taskservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-api", url = "http://user-api:8080")
public interface UserClient {

    @GetMapping("/users/check-existing/{id}")
    Boolean checkUserExisting(@PathVariable("id") Long id);
}
