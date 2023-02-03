package com.thullo.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileId;

    private String fileName;
    private String fileType;

    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "file_byte", nullable = false, columnDefinition = "mediumblob")
    private byte[] fileByte;
    public FileData(String fileName, byte[] fileByte){
        this.fileName = fileName;
        this.fileByte = fileByte;
    }
    public FileData(String fileId, String fileName, String fileType, byte[] fileByte) {
        this(fileName, fileByte);
        this.fileId = fileId;
        this.fileType = fileType;
    }



}
