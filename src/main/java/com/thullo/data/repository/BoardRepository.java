package com.thullo.data.repository;

import com.thullo.data.model.Board;
import com.thullo.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b WHERE b.user = :user ORDER BY b.createdAt DESC")
    List<Board> getAllByUserOrderByCreatedAtAsc(@Param("user") User user);
}
