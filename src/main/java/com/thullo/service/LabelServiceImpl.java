package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.Label;
import com.thullo.data.model.Task;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.LabelRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.LabelRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService{
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;

    private final BoardRepository boardRepository;

    private final ModelMapper mapper;
    @Override
    public Label createLabel(LabelRequest request) throws ResourceNotFoundException {
        Task task = getTask(request.getBoardRef());
        Label label = mapper.map(request, Label.class);
        label.addTask(task);
        Label savedLabel = labelRepository.save(label);
        task.addLabel(savedLabel);
        taskRepository.save(task);
        return savedLabel;
    }

    private Task getTask(String boardRef) throws ResourceNotFoundException {
        return taskRepository.findByBoardRef(boardRef).orElseThrow(
                () -> new ResourceNotFoundException(format("Task with board ref %s not found !", boardRef)));
    }

    @Override
    public void removeLabelFromTask(Long labelId, String boardRef) throws ResourceNotFoundException {
        Label label = getLabel(labelId);
        Task task = getTask(boardRef);

        label.getTasks().remove(task);
        task.getLabels().remove(label);

        labelRepository.save(label);
        taskRepository.save(task);
    }

    @Override
    public List<Label> getBoardLabel(Long boardId) throws ResourceNotFoundException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        return board.getTaskColumns()
                .stream()
                .flatMap(taskColumn -> taskColumn.getTasks().stream())
                .flatMap(task -> task.getLabels().stream())
                .collect(Collectors.toList());
    }

    private Label getLabel(Long labelId) throws ResourceNotFoundException {
        return labelRepository.findById(labelId).orElseThrow(
                () -> new ResourceNotFoundException("Label not found"));
    }
}
