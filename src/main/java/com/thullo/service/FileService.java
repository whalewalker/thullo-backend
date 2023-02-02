package com.thullo.service;

import com.thullo.data.model.FileData;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String  uploadFile(MultipartFile file, String url);
    byte[] decompressFile(byte[] compressedFile) throws IOException;
    FileData getFIle(String fileId) throws IOException;
    MediaType getMediaTypeForFileType(String fileType);
}
