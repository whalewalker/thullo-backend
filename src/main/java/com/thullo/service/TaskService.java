package com.thullo.service;

import com.thullo.data.model.Task;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.TaskRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface TaskService {
    Task createTask(String boardTag, String createdBy, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;

    Task moveTask(String boardRef, String status, Long index) throws ResourceNotFoundException;

    Task editTask(String boardRef, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;

    Task getTask(String boardRef) throws ResourceNotFoundException;
    void deleteTask(String boardRef) throws ResourceNotFoundException;

    List<Task> searchTask(String params);

    void addContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException;

    void removeContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException;

    Task updateTaskImage(String boardRef, MultipartFile coverImage, String requestUrl) throws ResourceNotFoundException, BadRequestException, IOException;

    String getTaskImageUrl(String boardRef) throws ResourceNotFoundException;
}

