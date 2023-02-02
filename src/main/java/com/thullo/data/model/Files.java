package com.thullo.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileId;

    private String fileName;

    @Lob
    @Column(name = "file_data", nullable = false, columnDefinition = "mediumblob")
    private byte[] fileData;
    public Files(String fileName, byte[] fileData){
        this.fileName = fileName;
        this.fileData = fileData;
    }
    public Files(String fileId, String fileName,  byte[] fileData) {
        this(fileName, fileData);
        this.fileId = fileId;
    }


}
