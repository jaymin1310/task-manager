package com.jaymin.taskmanager.dto.tasks;

import com.jaymin.taskmanager.entity.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Status status;
    private LocalDate dueDate;
}
