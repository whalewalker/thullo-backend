package com.thullo.data.repository;

import com.thullo.data.model.Comment;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @CachePut(key = "#result.id", value = "comments")
    <S extends Comment> S save(S comment);

    @Override
    @CacheEvict(key = "comment.id", value = "comments")
    void delete(Comment comment);

    @Override
    @Cacheable(key = "commentId", value = "comments")
    Optional<Comment> findById(Long commentId);
}
