package com.thullo.data.repository;

import com.thullo.data.model.Board;
import com.thullo.data.model.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {
    boolean existsByNameAndBoard(String name, Board board);

    @Query("select t from TaskColumn t where t.id = ?1 or upper(t.name) = upper(?2) and t.board = ?3")
    Optional<TaskColumn> findTaskByParams(@Nullable Long id, @Nullable String name, @NonNull Board board);
}
