package com.thullo.service;

import com.thullo.data.model.Task;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.RecordNotFoundException;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;

import java.io.IOException;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest) throws BadRequestException, IOException;
    Task moveTask(Long taskId, Long newColumnId, Long index) throws RecordNotFoundException;

    Task editTask(Long taskId, TaskRequest taskRequest);
}
