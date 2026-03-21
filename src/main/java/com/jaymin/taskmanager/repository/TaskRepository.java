package com.jaymin.taskmanager.repository;

import com.jaymin.taskmanager.entity.Status;
import com.jaymin.taskmanager.entity.Task;
import com.jaymin.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndStatus(User user, Status status);
    List<Task> findByUserAndDueDateBefore(User user, LocalDate dueDate);
}
