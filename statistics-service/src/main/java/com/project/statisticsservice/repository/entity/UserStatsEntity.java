package com.project.statisticsservice.repository.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_stats")
public class UserStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(unique = true, nullable = false)
    private Long userId;

    private int totalCreated = 0;
    private int totalAssigned = 0;

    @Column(name = "todo_count")
    private int todoCount = 0;

    private int inProgressCount = 0;
    private int doneCount = 0;

    private int lowPriorityCount = 0;
    private int mediumPriorityCount = 0;
    private int highPriorityCount = 0;

}
