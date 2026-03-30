package com.jaymin.taskmanager.service;

import com.jaymin.taskmanager.dto.tasks.CreateTaskRequest;
import com.jaymin.taskmanager.dto.tasks.TaskResponse;
import com.jaymin.taskmanager.dto.tasks.TaskStatsResponse;
import com.jaymin.taskmanager.dto.tasks.UpdateTaskRequest;
import com.jaymin.taskmanager.entity.Status;
import com.jaymin.taskmanager.entity.Task;
import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.TaskRepository;
import com.jaymin.taskmanager.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
//    public List<TaskResponse> getAllTasks() {
//        User user=getCurrentUser();
//        List<Task>tasks=taskRepository.findByUser(user);
//        return tasks.stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
    //get all tasks by page and its handle all filters with optional without adding multiple end points
    public Page<TaskResponse> getAllTasks(Status status, LocalDate dueBefore, int page, int size) {
        User user=getCurrentUser();
        Page<Task>tasks;
        Pageable pageable= PageRequest.of(page,size);
        if(status!=null && dueBefore!=null){
            tasks=taskRepository.findByUserAndStatusAndDueDateBefore(user,status,dueBefore,pageable);
        }else if(status!=null){
            tasks=taskRepository.findByUserAndStatus(user,status,pageable);
        }else if(dueBefore!=null){
            tasks=taskRepository.findByUserAndDueDateBefore(user,dueBefore,pageable);
        }else{
            tasks=taskRepository.findByUser(user,pageable);
        }
        return tasks.map(this::mapToResponse);
    }

    //get completed and pending tasks
   /* public Page<TaskResponse> getTasksByStatus(Status status,int page,int size){
        User user=getCurrentUser();
        Pageable pageable= PageRequest.of(page,size);
        Page<Task>tasks=taskRepository.findByUserAndStatus(user,status,pageable);
        return tasks.map(this::mapToResponse);
    }*/
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
    public List<TaskResponse>getOverdueTasks() {
        User user = getCurrentUser();
        List<Task> tasks = taskRepository.findByUserAndDueDateBeforeAndStatusNot(user, LocalDate.now(), Status.COMPLETED);
        return tasks.stream()
                .map(this::mapToResponse)
                .toList();
    }
    public TaskStatsResponse getTaskStats() {
        User user=getCurrentUser();
        long total=taskRepository.countByUser(user);
        long completed=taskRepository.countByUserAndStatus(user, Status.COMPLETED);
        long pending=taskRepository.countByUserAndStatus(user, Status.PENDING);
        return TaskStatsResponse.builder()
                .total(total)
                .pending(pending)
                .completed(completed)
                .build();
    }
    //search by keyword for title or description
    public Page<TaskResponse> searchTasks(String keyword,int page,int size) {
        User user=getCurrentUser();
        Pageable pageable= PageRequest.of(page,size);
        Page<Task>tasks=taskRepository.findByUserAndTitleContainingIgnoreCaseOrUserAndDescriptionContainingIgnoreCase(user,keyword,user,keyword,pageable);
        return tasks.map(this::mapToResponse);
    }
    //private services...........................
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
