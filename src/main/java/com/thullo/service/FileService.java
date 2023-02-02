package com.thullo.service;

import com.thullo.data.model.Files;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String  uploadFile(MultipartFile file);
    byte[] decompressFile(byte[] compressedFile) throws IOException;
    Files getFIle(String fileId) throws IOException;
}
