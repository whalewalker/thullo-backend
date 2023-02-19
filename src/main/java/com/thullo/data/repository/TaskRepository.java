package com.thullo.data.repository;

import com.thullo.data.model.Board;
import com.thullo.data.model.Status;
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
    @Query("SELECT t FROM Task t WHERE t.name LIKE %:search% OR t.boardRef = :boardRef")
    List<Task> findByNameContainingOrBoardRef(@Param("search") String search, @Param("boardRef") String boardRef);

    Optional<Task> findByBoardRef(@NonNull String boardRef);

    long countByBoardAndStatus(Board board, Status status);
}
