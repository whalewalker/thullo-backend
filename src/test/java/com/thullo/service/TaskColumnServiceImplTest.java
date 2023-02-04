package com.thullo.service;

import com.thullo.data.model.Task;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.repository.TaskColumnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * should[ExpectedBehavior]When[Scenario]: This format describes the expected behavior and the scenario being tested. For example: shouldReturnCorrectSumWhenPositiveNumbers.
 */
@ExtendWith(MockitoExtension.class)
class TaskColumnServiceImplTest {

    @Mock
    private TaskColumnRepository taskColumnRepository;

    @InjectMocks
    private TaskColumnServiceImpl taskService;

    private TaskColumn taskColumn;

    @BeforeEach
    void setUp() {
        taskColumn = new TaskColumn();
    }

    @Test
    void shouldReturnTaskColumnWhenValidId() {
        when(taskColumnRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(taskColumn));

        TaskColumn actualTaskColumn = taskService.getCurrentTaskColumn(1L);

        verify(taskColumnRepository).findById(1L);
        assertNotNull(actualTaskColumn);
    }

    @Test
    void shouldReturnNullWhenInValidId() {
        when(taskColumnRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        TaskColumn actualTaskColumn = taskService.getCurrentTaskColumn(1L);

        verify(taskColumnRepository).findById(1L);
        assertNull(actualTaskColumn);
    }

    @Test
    void shouldReturnListOfTaskWhenValidId() {
        taskColumn.setName("Backlog");
        taskColumn.getTasks().addAll(List.of(
                new Task("create repo"),
                new Task("create issue"),
                new Task("implement issue")));

        when(taskColumnRepository.findById(anyLong()))
                .thenReturn(Optional.of(taskColumn));

        TaskColumn actualTaskColumn = taskService.getCurrentTaskColumn(1L);

        verify(taskColumnRepository).findById(1L);
        assertAll(() -> {
            assertEquals("Backlog", actualTaskColumn.getName());
            assertEquals(3, actualTaskColumn.getTasks().size());
            assertEquals(3, actualTaskColumn.getTasks().size());

            for (Task task : actualTaskColumn.getTasks()) {
                assertNotNull(task);
            }
        });

    }
}