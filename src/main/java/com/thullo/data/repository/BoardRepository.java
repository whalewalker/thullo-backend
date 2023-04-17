package com.thullo.data.repository;

import com.thullo.data.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    @Query("SELECT b FROM Board b JOIN b.createdBy u WHERE u.email = :userEmail ORDER BY b.createdAt DESC")
    List<Board> findAllByCreatedBy(@Param("userEmail") String userEmail);

    @Query(value = "SELECT DISTINCT b FROM Board b JOIN b.tasks t JOIN t.contributors c WHERE c.email = :userEmail")
    List<Board> findAllByContributors(@Param("userEmail") String userEmail);

    @Query(value = "SELECT DISTINCT b FROM Board b JOIN FETCH b.collaborators c WHERE UPPER(c.email) = UPPER(:userEmail)")
    List<Board> findAllByCollaborators(@Param("userEmail") String userEmail);

    @Query("SELECT b FROM Board b WHERE UPPER(b.boardTag) = UPPER(:boardTag)")
    Optional<Board> findByBoardTag(@NonNull String boardTag);

}