package com.thullo.data.repository;

import com.thullo.data.model.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {
}
