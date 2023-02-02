package com.thullo.data.repository;

import com.thullo.data.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {
    Files getFilesByFileId(String fileId);

}