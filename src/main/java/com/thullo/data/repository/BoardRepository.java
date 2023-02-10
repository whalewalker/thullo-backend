package com.thullo.data.repository;

import com.thullo.data.model.Board;
import com.thullo.data.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Cacheable(value = "boardsByUser", key = "#user.id")
    List<Board> getAllByUserOrderByCreatedAtDesc(User user);
    @Override
    @Cacheable(value = "boards", key = "#id")
    Optional<Board> findById(Long id);

    @Override
    @CacheEvict(value = "boards", key = "#board.id")
    Board save(Board board);

    @Override
    @CacheEvict(value = "boards", key = "#id")
    void deleteById(Long id);
}
