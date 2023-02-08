package com.thullo.data.repository;

import com.thullo.data.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM task WHERE task_column_id = ?1 ORDER BY `position` ASC", nativeQuery = true)
    Optional<List<Task>> findByTaskColumnOrderByPositionAsc(long taskColumnId);
}
