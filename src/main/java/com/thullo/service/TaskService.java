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
    Task createTask(TaskRequest taskRequest) throws BadRequestException, IOException;
    Task moveTask(Long taskId, Long newColumnId, Long index) throws ResourceNotFoundException;
    Task editTask(Long taskId, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException;
   Task getTask(Long taskId) throws ResourceNotFoundException;
    void deleteTask(Long taskId);
    List<Task> findTaskContainingNameOrBoardId(String name, String boardId);
   void addContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException;
   void removeContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException;

   Task updateTaskImage(String boardRef, MultipartFile coverImage, String requestUrl) throws ResourceNotFoundException, BadRequestException, IOException;

    String getTaskImageUrl(String boardRef) throws ResourceNotFoundException;
}

