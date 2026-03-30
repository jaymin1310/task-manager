package com.jaymin.taskmanager.repository;

import com.jaymin.taskmanager.entity.Status;
import com.jaymin.taskmanager.entity.Task;
import com.jaymin.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUser(User user, Pageable page);

    Page<Task> findByUserAndStatus(User user, Status status, Pageable page);

    Page<Task> findByUserAndDueDateBefore(User user, LocalDate dueDate, Pageable page);

    Page<Task> findByUserAndStatusAndDueDateBefore(User user, Status status, LocalDate dueDate, Pageable page);

    Page<Task> findByUserAndTitleContainingIgnoreCaseOrUserAndDescriptionContainingIgnoreCase(User user1, String title, User user2, String description, Pageable page);

    List<Task> findByUserAndDueDateBeforeAndStatusNot(User user, LocalDate dueDate, Status status);

    long countByUser(User user);

    long countByUserAndStatus(User user, Status status);
}