package com.thullo.service;

import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
}
