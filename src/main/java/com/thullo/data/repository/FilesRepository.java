package com.thullo.data.repository;

import com.thullo.data.model.FileData;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<FileData, Long> {
    @Cacheable(key = "fileId", value = "files")
    Optional<FileData> findFileDataByFileId(String fileId);

    @CachePut(key = "#result.fileId", value = "files")
    <S extends FileData> S save(S fileData);
}