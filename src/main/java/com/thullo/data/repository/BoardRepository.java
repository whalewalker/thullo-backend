package com.thullo.data.repository;

import com.thullo.data.model.Board;
import com.thullo.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> getAllByUserOrderByCreatedAtAsc(User user);
}
