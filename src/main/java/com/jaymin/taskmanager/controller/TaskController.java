package com.jaymin.taskmanager.controller;

import com.jaymin.taskmanager.dto.tasks.CreateTaskRequest;
import com.jaymin.taskmanager.dto.tasks.TaskResponse;
import com.jaymin.taskmanager.dto.tasks.TaskStatsResponse;
import com.jaymin.taskmanager.dto.tasks.UpdateTaskRequest;
import com.jaymin.taskmanager.entity.Status;
import com.jaymin.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;
    int MAX_SIZE=50;
    int MAX_PAGE=100;
    //create task
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        return ResponseEntity.ok(taskService.createTask(createTaskRequest));
    }
//    //get All task
//    @GetMapping
//    public ResponseEntity<List<TaskResponse>> getAllTasks() {
//        return ResponseEntity.ok(taskService.getAllTasks());
//    }
    //get tasks of page size
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required=false)Status status,
            @RequestParam(required=false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dueDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue= "10") int size

    ) {
        if(page>MAX_PAGE){
            throw new IllegalArgumentException("Page too large");
        }
        size=Math.min(size,MAX_SIZE);
        return ResponseEntity.ok(taskService.getAllTasks(status,dueDate,page, size));
    }
    //get task by status(Not needed now cause i already decalre dynamic filter above this consist in that)
//    @GetMapping("/status")
//    public ResponseEntity<Page<TaskResponse>> getTasksByStatus(
//            @RequestParam Status status,
//            @RequestParam(defaultValue = "0")int page,
//            @RequestParam(defaultValue = "10")int size
//    ) {
//        if(page>MAX_PAGE){
//            throw new IllegalArgumentException("Page too large");
//        }
//        size=Math.min(size,MAX_SIZE);
//        return  ResponseEntity.ok(taskService.getTasksByStatus(status,page,size));
//    }
    //get task by id
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
    //update task by id
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest updateTaskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, updateTaskRequest));
    }
    //delete task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Delete Successfully");
    }
    //get overDue task
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }
    //get tasks stats
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponse> getTaskStats() {
        return ResponseEntity.ok(taskService.getTaskStats());
    }
    //get tasks by search keywords either in description or title
    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> getTasksBySearch(
    @RequestParam String keyword,
    @RequestParam(defaultValue = "0")int page,
    @RequestParam(defaultValue = "10")int size
    ){
        return ResponseEntity.ok(taskService.searchTasks(keyword,page,size));
    }


}
