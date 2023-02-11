package com.thullo.data.repository;

import com.thullo.data.model.FileData;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<FileData, Long> {
    @CachePut(value = "files", key = "#fileData.fileId")
    @Override
    FileData save(FileData fileData);

    @Cacheable(value = "files", key = "#fileId")
    FileData getFilesByFileId(String fileId);

}