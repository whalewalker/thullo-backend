package com.thullo.data.repository;

import com.thullo.data.model.BoardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface BoardIdRepository extends JpaRepository<BoardId, Long> {
    @Query("select b from BoardId b where upper(b.boardTag) = upper(?1)")
    Optional<BoardId> findByBoardTag(@NonNull String boardTag);

}
