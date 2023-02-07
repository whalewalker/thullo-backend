package com.thullo.service;

import com.thullo.web.exception.BadRequestException;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;

import java.io.IOException;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest) throws BadRequestException, IOException;
}
