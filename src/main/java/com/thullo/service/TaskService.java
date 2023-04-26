package com.thullo.service;

import com.thullo.data.model.Attachment;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.response.TaskResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskService {
    TaskResponse createTask(String boardTag, String createdBy, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;

    TaskResponse moveTask(String boardRef, Long columnId, Long position) throws ResourceNotFoundException;

    TaskResponse editTask(String boardRef, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;

    TaskResponse getTask(String boardRef) throws ResourceNotFoundException;

    void deleteTask(String boardRef) throws ResourceNotFoundException;

    List<TaskResponse> searchTask(String params);

    void addContributor(String boardRef, String contributorEmail) throws ResourceNotFoundException, UserException;

    void removeContributor(String boardRef, String contributorEmail) throws ResourceNotFoundException, UserException;

    TaskResponse updateTaskImage(String boardRef, MultipartFile coverImage, String requestUrl) throws ResourceNotFoundException, BadRequestException, IOException;

    String getTaskImageUrl(String boardRef) throws ResourceNotFoundException;

    Attachment addAttachmentToTask(String boardRef, String url, MultipartFile file) throws ResourceNotFoundException, BadRequestException, IOException;

    void deleteAttachmentFromTask(String fileId, Long attachmentId);
}

