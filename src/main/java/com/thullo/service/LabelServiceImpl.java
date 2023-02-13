package com.thullo.service;

import com.thullo.data.model.Label;
import com.thullo.data.model.Task;
import com.thullo.data.repository.LabelRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.LabelRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService{
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;

    private final ModelMapper mapper;
    @Override
    public Label createLabel(LabelRequest request) throws ResourceNotFoundException {
        Task task = taskRepository.findByBoardRef(request.getBoardRef()).orElseThrow(
                ()-> new ResourceNotFoundException(format("Task with board ref %s not found !", request.getBoardRef())));
        Label label = mapper.map(request, Label.class);
        label.addTask(task);
        Label savedLabel = labelRepository.save(label);
        task.addLabel(savedLabel);
        taskRepository.save(task);
        return savedLabel;
    }
}
