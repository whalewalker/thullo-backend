package com.thullo.service;

import com.thullo.data.model.UUIDWrapper;
import com.thullo.data.model.Files;
import com.thullo.data.repository.FilesRepository;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FilesServiceImplTest {
    @Mock
    private UUIDWrapper uuidWrapper;

    @Mock
    private FilesRepository filesRepository;

    @InjectMocks
    private FileServiceImpl fileService;


    @Test
    void testUploadFile_withValidFile_thenGeneratedUrl() throws IOException {
        when(uuidWrapper.getUUID()).thenReturn("123e4567-e89b-12d3-a456-426655440000");
        when(filesRepository.save(any())).thenReturn(new Files());
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/code.png");
        // mock the call to UUIDWrapper.getUUID() to return the mocked UUID
        String imageUrl = fileService.uploadFile(multipartFile, "http://localhost:8080/api/v1/thullo/upload");
        assertEquals("http://localhost:8080/api/v1/thullo/files/" + uuidWrapper.getUUID(), imageUrl);
    }

    public MultipartFile getMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    }
}