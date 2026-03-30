package com.jaymin.taskmanager.dto.tasks;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskStatsResponse {
    private long total;
    private long completed;
    private long pending;
}
