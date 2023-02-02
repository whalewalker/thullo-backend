package com.thullo.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Lob
    @Column(name = "file_data", nullable = false, columnDefinition = "mediumblob")
    private byte[] fileData;
    public FileData(String fileName, byte[] fileData){
        this.fileName = fileName;
        this.fileData = fileData;
    }
    public FileData(String fileId, String fileName, String fileType, byte[] fileData) {
        this(fileName, fileData);
        this.fileId = fileId;
        this.fileType = fileType;
    }



}
