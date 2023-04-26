package com.thullo.data.repository;

import com.thullo.data.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.name LIKE %:params% OR t.boardRef LIKE %:params%")
    List<Task> findByParams(@Param("params") String params);

    @Query("SELECT t FROM Task t WHERE t.taskColumn.id = ?1 ORDER BY t.position ASC")
    List<Task> findByTaskColumnOrderByPositionAsc(long taskColumnId);

    Optional<Task> findByBoardRef(@NonNull String boardRef);
}
