package com.thullo.service;

import com.thullo.data.model.FileData;
import com.thullo.data.repository.FilesRepository;
import com.thullo.web.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;

    @Override
    public String uploadFile(MultipartFile file, String url) throws BadRequestException, IOException {
        if(file.isEmpty()) throw new BadRequestException("File cannot be empty");
        String baseUrl = url.substring(0, url.lastIndexOf("thullo"));
        FileData fileData = uploadFileData(file);
        return format("%sthullo/files/%s", baseUrl, fileData.getFileId());
    }

    private FileData uploadFileData(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String fileType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        byte[] compressedFile = compressFile(file.getBytes());
        String fileId = UUID.randomUUID().toString();
        InputStream is = new ByteArrayInputStream(compressedFile);
        FileData fileData = new FileData(fileId, originalFileName, fileType, is.readAllBytes());
        return filesRepository.save(fileData);
    }


    private byte[] decompressFile(byte[] compressedFile) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedFile);
        GZIPInputStream gzipIn = new GZIPInputStream(bais);
        return getBytes(gzipIn);
    }

    @Override
    public FileData getFIle(String fileId) throws IOException {
        FileData dbFile = filesRepository.findFileDataByFileId(fileId).orElse(null);
        if (dbFile != null) {
            byte[] compressedFile = dbFile.getFileByte();
            byte[] decompressedFile = decompressFile(compressedFile);
            dbFile.setFileByte(decompressedFile);
        }
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

    @Override
    public void deleteFile(String fileId) {
        filesRepository.findFileDataByFileId(fileId)
                .ifPresent(filesRepository::delete);
    }


}
