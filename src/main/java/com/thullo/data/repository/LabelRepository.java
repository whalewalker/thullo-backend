package com.thullo.data.repository;

import com.thullo.data.model.Label;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {
    @Query("select l from Label l where upper(l.name) = upper(?1)")
    Optional<Label> findByName(@NonNull String name);

    @Override
    @Cacheable(key = "boardId", value = "labels")
    Optional<Label> findById(Long boardId);

    @CachePut(key = "#result.id", value = "labels")
    <S extends Label> S save(S label);
}
