package com.project.statisticsservice.api.controller;


import com.project.statisticsservice.api.dto.user.UserStatsDto;
import com.project.statisticsservice.api.service.userapi.UserStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stats/user")
public class UserStatisticController {

    private final UserStatisticService userStatisticService;

    @GetMapping("/{userId}")
    public UserStatsDto getUserStatsById(@PathVariable Long userId){
        return userStatisticService.getUserStatsById(userId);
    }

}
