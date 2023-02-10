package com.thullo.service;

import com.thullo.data.model.TaskColumn;
import com.thullo.data.repository.TaskColumnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskColumnServiceImpl implements TaskColumnService {
    private final TaskColumnRepository taskColumnRepository;

    @Cacheable(value = "taskColumns", key = "#taskColumnId")
    public TaskColumn getCurrentTaskColumn(Long taskColumnId) {
        return taskColumnRepository.findById(taskColumnId).orElse(null);
    }
}
