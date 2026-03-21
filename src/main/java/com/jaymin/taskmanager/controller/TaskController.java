package com.jaymin.taskmanager.controller;

import com.jaymin.taskmanager.dto.tasks.CreateTaskRequest;
import com.jaymin.taskmanager.dto.tasks.TaskResponse;
import com.jaymin.taskmanager.dto.tasks.UpdateTaskRequest;
import com.jaymin.taskmanager.entity.Status;
import com.jaymin.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        return ResponseEntity.ok(taskService.createTask(createTaskRequest));
    }
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    @GetMapping("/status")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@RequestBody Status status) {
        return  ResponseEntity.ok(taskService.getTasksByStatus(status));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest updateTaskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, updateTaskRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Delete Successfully");
    }
}
