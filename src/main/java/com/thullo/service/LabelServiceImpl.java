package com.thullo.service;

import com.thullo.data.model.Label;
import com.thullo.data.model.Task;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.repository.LabelRepository;
import com.thullo.data.repository.TaskColumnRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.LabelRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.thullo.util.Helper.isNullOrEmpty;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final TaskColumnRepository taskColumnRepository;

    private final ModelMapper mapper;

    @Override
    public Label createLabel(String boardRef, LabelRequest request) throws ResourceNotFoundException {
        Task task = getTask(boardRef);
        Label label = labelRepository.findByName(request.getName())
                .orElseGet(() -> createNewLabel(request));

        if (!task.getLabels().contains(label)) {
            task.getLabels().add(label);
            taskRepository.save(task);
        }
        return label;
    }

    private Label createNewLabel(LabelRequest request) {
        Label label = new Label(request.getName(), request.getColorCode(), request.getBackgroundCode());
        return labelRepository.save(label);
    }


    private Task getTask(String boardRef) throws ResourceNotFoundException {
        return taskRepository.findByBoardRef(boardRef).orElseThrow(
                () -> new ResourceNotFoundException(format("Task with board ref %s not found", boardRef)));
    }

    @Override
    public void removeLabelFromTask(Long labelId, String boardRef) throws ResourceNotFoundException {
        Label label = getLabel(labelId);
        Task task = getTask(boardRef);

        task.getLabels().remove(label);
        taskRepository.save(task);
    }

    @Override
    public List<Label> getTaskColumnLabel(Long columnId) throws ResourceNotFoundException {
        TaskColumn taskColumn = findTaskColumnById(columnId);

        return taskColumn.getTasks()
                .stream()
                .flatMap(task -> task.getLabels().stream())
                .collect(Collectors.toList());
    }

    private TaskColumn findTaskColumnById(Long columnId) throws ResourceNotFoundException {
        return taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Task column not found"));
    }


    public Label updateLabelOnTask(String boardRef, Long labelId, LabelRequest request) throws ResourceNotFoundException {
        Task task = getTask(boardRef);
        Label label = getLabel(labelId);

        if (!isNullOrEmpty(request.getName()) && !label.getName().equals(request.getName())) {
            // Request has a new name different from the existing label name.
            Label existingLabel = labelRepository.findByName(request.getName()).orElse(null);
            if (existingLabel == null) {
                // Create a new label with the new name.
                existingLabel = new Label();
                mapper.map(label, existingLabel);
                mapper.map(request, existingLabel);
                existingLabel.setId(null);
                existingLabel = labelRepository.save(existingLabel);
            }
            // Update the task label to the existing label with the new name.
            task.getLabels().remove(label);
            task.getLabels().add(existingLabel);
            taskRepository.save(task);
            return existingLabel;
        } else if (!isNullOrEmpty(request.getColorCode()) || !isNullOrEmpty(request.getBackgroundCode())) {
            // Request has a new color code or background code, but the name is the same as the existing label name.
            Label newLabel = new Label();
            mapper.map(label, newLabel);
            mapper.map(request, newLabel);
            newLabel.setId(null);
            newLabel = labelRepository.save(newLabel);
            // Update the task label to the new label with the new color code or background code.
            task.getLabels().remove(label);
            task.getLabels().add(newLabel);
            taskRepository.save(task);
            return newLabel;
        } else {
            // Request has no changes to the name, color code, or background code.
            return label;
        }
    }


    private Label getLabel(Long labelId) throws ResourceNotFoundException {
        return labelRepository.findById(labelId).orElseThrow(
                () -> new ResourceNotFoundException("Label not found"));
    }
}
