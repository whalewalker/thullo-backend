package com.thullo.data.repository;

import com.thullo.data.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<FileData, Long> {
    Optional<FileData> getFilesByFileId(String fileId);
}