package com.thullo.service;

import com.thullo.data.model.Task;
import com.thullo.data.repository.TaskRepository;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ModelMapper mapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private TaskServiceImpl taskService;


    private TaskRequest taskRequest;
    private TaskResponse taskResponse;
    private Task task;
    String taskName = "First task";
    String imageUrl = "http://localhost:8080/api/v1/thullo/files/123e4567-e89b-12d3-a456-426655440000";
    @BeforeEach
    void setUp() {
        task = new Task();
        task.setName(taskName);


        taskRequest = new TaskRequest();
        taskRequest.setName(taskName);
        taskRequest.setTask(task);


        taskResponse = new TaskResponse();
        taskResponse.setName(taskName);
        taskResponse.setImageUrl(imageUrl);
    }

    @Test
    void testCreateTask_withValidName_thenTaskIsCreated(){

        when(taskRepository.save(any())).thenReturn(task);

        when(mapper.map(task, TaskResponse.class))
                .thenReturn(taskResponse);

        TaskResponse actualResponse = taskService.createTask(taskRequest);

        verify(mapper).map(task, TaskResponse.class);
        verify(taskRepository).save(task);
        Assertions.assertEquals(taskName, actualResponse.getName());
    }

    @Test
    void testCreateTask_withCoverImage_thenTaskIsCreated() throws IOException {
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/code.png");
        String url = "http://localhost:8080/api/v1/thullo";
        taskRequest.setFile(multipartFile);
        taskRequest.setRequestUrl(url);


        when(taskRepository.save(any())).thenReturn(task);

        when(mapper.map(task, TaskResponse.class))
                .thenReturn(taskResponse);


        when(fileService.uploadFile(taskRequest.getFile(), taskRequest.getRequestUrl()))
                .thenReturn(imageUrl);

        TaskResponse actualResponse = taskService.createTask(taskRequest);
        verify(fileService).uploadFile(multipartFile, url);
        assertEquals(taskName, actualResponse.getName());
        assertEquals(imageUrl, actualResponse.getImageUrl());
    }


    public MultipartFile getMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    }
}