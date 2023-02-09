package com.thullo.service;

import com.thullo.data.model.Task;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.RecordNotFoundException;
import com.thullo.web.payload.request.TaskRequest;

import java.io.IOException;

public interface TaskService {
    Task createTask(TaskRequest taskRequest) throws BadRequestException, IOException;
    com.thullo.data.model.Task moveTask(Long taskId, Long newColumnId, Long index) throws RecordNotFoundException;

    com.thullo.data.model.Task editTask(Long taskId, TaskRequest taskRequest) throws BadRequestException, IOException, RecordNotFoundException;
   public Task getTask(Long taskId) throws RecordNotFoundException;
}
