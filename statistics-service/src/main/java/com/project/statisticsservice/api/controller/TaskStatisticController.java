package com.project.statisticsservice.api.controller;

import com.project.statisticsservice.api.dto.task.TaskStatsDto;
import com.project.statisticsservice.api.service.taskapi.TaskStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stats/task")
public class TaskStatisticController {

    private final TaskStatisticService statisticService;

    @GetMapping()
    public TaskStatsDto getAllTaskStatistic() {
        return statisticService.getAllTaskStatistic();
    }
}
