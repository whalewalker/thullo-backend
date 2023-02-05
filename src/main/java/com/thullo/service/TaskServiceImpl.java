package com.thullo.service;

import com.thullo.data.model.Task;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.repository.TaskColumnRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final FileService fileService;

    private final TaskColumnRepository taskColumnRepository;

    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskRequest.getTask();
        String imageUrl = fileService.uploadFile(taskRequest.getFile(), taskRequest.getRequestUrl());
        task.setImageUrl(imageUrl);
        Task saveTask = taskRepository.save(task);
        return mapper.map(saveTask, TaskResponse.class);
    }

    public boolean isTaskOwner(Long taskColumnId, String email) {
        TaskColumn taskColumn = getTaskColumn(taskColumnId);
        return taskColumn.getBoard().getUser().getEmail().equals(email);
    }

    private TaskColumn getTaskColumn(Long taskColumnId) {
        return taskColumnRepository.findById(taskColumnId).orElseThrow( ()-> new BadRequestException("Task not found!"));
    }

}
