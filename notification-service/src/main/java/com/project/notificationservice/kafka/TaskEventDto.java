package com.project.notificationservice.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEventDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("creatorId")
    private Long creatorId;

    @JsonProperty("assignedUserId")
    private Long assignedUserId;

    @JsonProperty("taskStatus")
    private String taskStatus;

    @JsonProperty("createDateTime")
    private Long createDateTime;

    @JsonProperty("deadlineDate")
    private Long deadlineDate;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("doneDateTime")
    private Long doneDateTime;
}
