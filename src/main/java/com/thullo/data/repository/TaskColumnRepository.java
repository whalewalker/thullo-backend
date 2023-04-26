package com.thullo.data.repository;

import com.thullo.data.model.TaskColumn;
import com.thullo.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {
    boolean existsByNameAndCreatedBy(String name, User user);
}
