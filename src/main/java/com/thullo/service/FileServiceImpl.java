package com.thullo.service;

import com.thullo.data.model.Files;
import com.thullo.data.model.UUIDWrapper;
import com.thullo.data.repository.FilesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;
    private final UUIDWrapper uuidWrapper;

    @Override
    public String uploadFile(MultipartFile file, String url) {
        try {
            byte[] compressedFile = compressFile(file.getBytes());
            String fileId = uuidWrapper.getUUID();
            String baseUrl = url.substring(0, url.lastIndexOf("/"));
            String fileUrl = format("%s/files/%s", baseUrl, fileId);
            InputStream is = new ByteArrayInputStream(compressedFile);
            filesRepository.save(new Files(fileId, file.getOriginalFilename(), is.readAllBytes()));

            return fileUrl;
        } catch (Exception e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

    @Override
    public byte[] decompressFile(byte[] compressedFile) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedFile);
        GZIPInputStream gzipIn = new GZIPInputStream(bais);
        return getBytes(gzipIn);
    }

    @Override
    public Files getFIle(String fileId) throws IOException {
        Files dbFile = filesRepository.getFilesByFileId(fileId);
        InputStream is = new ByteArrayInputStream(dbFile.getFileData());
        byte[] compressedFile = IOUtils.toByteArray(is);
        byte[] decompressedFile = decompressFile(compressedFile);
        return new Files(dbFile.getFileName(), decompressedFile);
    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        is.close();
        return baos.toByteArray();
    }

    private byte[] compressFile(byte[] fileData) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        gzipOut.write(fileData);
        gzipOut.close();
        return baos.toByteArray();
    }


}
