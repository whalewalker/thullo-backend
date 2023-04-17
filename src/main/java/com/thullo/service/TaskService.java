package com.thullo.service;

import com.thullo.data.model.Attachment;
import com.thullo.data.model.Task;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.StatusRequest;
import com.thullo.web.payload.request.TaskRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskService {
    Task createTask(String boardTag, String createdBy, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;

    Task moveTask(String boardRef, String status, Long index) throws ResourceNotFoundException;

    Task editTask(String boardRef, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;

    Task getTask(String boardRef) throws ResourceNotFoundException;

    void deleteTask(String boardRef) throws ResourceNotFoundException;

    List<Task> searchTask(String params);

    void addContributor(String boardRef, String contributorEmail) throws ResourceNotFoundException, UserException;

    void removeContributor(String boardRef, String contributorEmail) throws ResourceNotFoundException, UserException;

    Task updateTaskImage(String boardRef, MultipartFile coverImage, String requestUrl) throws ResourceNotFoundException, BadRequestException, IOException;

    String getTaskImageUrl(String boardRef) throws ResourceNotFoundException;

    Attachment addAttachmentToTask(String boardRef, String url, MultipartFile file) throws ResourceNotFoundException, BadRequestException, IOException;

    void deleteAttachmentFromTask(String fileId, Long attachmentId);

    List<Task> editStatus(StatusRequest request, String boardTag) throws ResourceNotFoundException;
    List<Task> deleteStatus(StatusRequest statusRequest, String boardTag) throws ResourceNotFoundException;
}

