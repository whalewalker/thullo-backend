package com.thullo.service;

import com.thullo.data.model.FileData;
import com.thullo.data.model.UUIDWrapper;
import com.thullo.data.repository.FilesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
            String originalFileName = file.getOriginalFilename();
            assert originalFileName != null;
            String fileType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

            byte[] compressedFile = compressFile(file.getBytes());
            String fileId = uuidWrapper.getUUID();
            String baseUrl = url.substring(0, url.lastIndexOf("thullo"));
            String fileUrl = format("%sthullo/files/%s", baseUrl, fileId);
            InputStream is = new ByteArrayInputStream(compressedFile);
            filesRepository.save(new FileData(fileId, originalFileName, fileType, is.readAllBytes()));

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
    public FileData getFIle(String fileId) throws IOException {
        FileData dbFile = filesRepository.getFilesByFileId(fileId);
        InputStream is = new ByteArrayInputStream(dbFile.getFileByte());
        byte[] compressedFile = IOUtils.toByteArray(is);
        byte[] decompressedFile = decompressFile(compressedFile);
        dbFile.setFileByte(decompressedFile);
        return dbFile;
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

    private byte[] compressFile(byte[] fileData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        gzipOut.write(fileData);
        gzipOut.close();
        return baos.toByteArray();
    }

    public  MediaType getMediaTypeForFileType(String fileType) {
        switch (fileType) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "csv":
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "xml":
                return MediaType.APPLICATION_XML;
            case "json":
                return MediaType.APPLICATION_JSON;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
