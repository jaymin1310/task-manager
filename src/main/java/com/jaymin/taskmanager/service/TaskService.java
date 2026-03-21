package com.jaymin.taskmanager.service;

import com.jaymin.taskmanager.dto.tasks.CreateTaskRequest;
import com.jaymin.taskmanager.dto.tasks.TaskResponse;
import com.jaymin.taskmanager.dto.tasks.UpdateTaskRequest;
import com.jaymin.taskmanager.entity.Status;
import com.jaymin.taskmanager.entity.Task;
import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.TaskRepository;
import com.jaymin.taskmanager.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    //create new task
    public TaskResponse createTask(CreateTaskRequest taskRequest) {
        User user=getCurrentUser();
        Task task=Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(Status.PENDING)
                .user(user)
                .createdAt(LocalDateTime.now())
                .dueDate(taskRequest.getDueDate())
                .updatedAt(LocalDateTime.now())
                .build();
        Task savedTask=taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    //get all tasks
    public List<TaskResponse> getAllTasks() {
        User user=getCurrentUser();
        List<Task>tasks=taskRepository.findByUser(user);
        return tasks.stream()
                .map(this::mapToResponse)
                .toList();
    }

    //get completed and pending tasks
    public List<TaskResponse> getTasksByStatus(Status status){
        User user=getCurrentUser();
        List<Task>tasks=taskRepository.findByUserAndStatus(user,status);
        return tasks.stream().map(this::mapToResponse).toList();
    }
    //get task by id
    public TaskResponse getTaskById(Long id) {
        Task task=getTaskOrThrow(id);
        return mapToResponse(task);
    }
    //update task
    public TaskResponse updateTask(Long taskId,UpdateTaskRequest updateTaskRequest) {
        Task task=getTaskOrThrow(taskId);
        if(updateTaskRequest.getTitle()!=null){
            task.setTitle(updateTaskRequest.getTitle());
        }
        if(updateTaskRequest.getDescription()!=null){
            task.setDescription(updateTaskRequest.getDescription());
        }
        if(updateTaskRequest.getDueDate()!=null){
            task.setDueDate(updateTaskRequest.getDueDate());
        }
        if(updateTaskRequest.getStatus()!=null){
            task.setStatus(updateTaskRequest.getStatus());
        }
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask=taskRepository.save(task);
        return mapToResponse(updatedTask);
    }
    //delete task
    public void deleteTask(Long id) {
        Task task=getTaskOrThrow(id);
        taskRepository.delete(task);
    }
    private Task getTaskOrThrow(Long id){
        User user=getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if(!task.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized user");
        }
        return task;
    }
    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
