package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.Task;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.util.Helper;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.StatusRequest;
import com.thullo.web.payload.request.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepositoryMock;
    @Mock
    private ModelMapper mapperMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private BoardRefGenerator boardRefGenerator;
    @Mock
    private BoardRepository boardRepositoryMock;
    @InjectMocks
    private TaskServiceImpl taskServiceMock;
    private Task expectedTask;
    private TaskRequest taskRequest;
    private final String boardRef = "board-1";

    @BeforeEach
    void setUp() {
        expectedTask = new Task();

        taskRequest = new TaskRequest();
        taskRequest.setName("Sample Task");
        taskRequest.setDescription("Sample Task Description");
        taskRequest.setStatus("BACKLOG");
    }

    @Test
    public void testCreateTaskCreatesTaskSuccessfully() throws Exception {
        //Setting up variables
        User user = new User();
        user.setEmail("test@test.com");
        Board board = new Board();
        board.setBoardTag("TAG");

        // set up repository mocks
        when(mapperMock.map(taskRequest, Task.class)).thenReturn(expectedTask);
        when(boardRepositoryMock.findByBoardTag("TAG")).thenReturn(Optional.of(board));
        when(userRepositoryMock.findUserByEmail(eq("test@test.com"))).thenReturn(user);
        when(taskRepositoryMock.countByBoardAndStatus(eq(board), eq("BACKLOG"))).thenReturn(1L);
        when(taskRepositoryMock.save(expectedTask)).thenReturn(expectedTask);

        doAnswer(invocation -> {
            mapperMock.map(taskRequest, expectedTask);
            expectedTask.setName(taskRequest.getName());
            expectedTask.setDescription(taskRequest.getDescription());
            expectedTask.setStatus(taskRequest.getStatus());
            expectedTask.setPosition(1L);
            expectedTask.setImageUrl("https://example.com/sample.txt");
            expectedTask.setBoardRef("TAG");
            expectedTask.setCreatedBy(user);
            expectedTask.setBoard(board);
            expectedTask.setBoardRef(boardRefGenerator.generateBoardRef(board));
            return expectedTask;
        }).when(taskRepositoryMock).save(expectedTask);

        // Creating a new Task
        Task actualTask = taskServiceMock.createTask("TAG", "test@test.com", taskRequest);

        // verify repository method calls
        verify(mapperMock).map(taskRequest, expectedTask);
        verify(taskRepositoryMock, times(1)).save(expectedTask);
        verify(userRepositoryMock, times(1)).findUserByEmail(any(String.class));
        verify(taskRepositoryMock, times(1)).countByBoardAndStatus(any(Board.class), any(String.class));

        // verify task created are as expected
        assertNotNull(actualTask);
        assertEquals(expectedTask, actualTask);
        assertEquals(taskRequest.getStatus(), actualTask.getStatus());
    }

    @Test
    public void testCreateTaskThrowsResourceNotFoundExceptionWhenBoardTagIsNotFound() {
        User user = new User();
        user.setEmail("test@test.com");

        Board board = new Board();
        when(mapperMock.map(taskRequest, Task.class)).thenReturn(expectedTask);

        // set up repository mocks
        when(boardRepositoryMock.findByBoardTag("TAG")).thenReturn(Optional.empty());
        when(userRepositoryMock.findUserByEmail(eq("test@test.com"))).thenReturn(user);
        when(taskRepositoryMock.countByBoardAndStatus(eq(board), eq("BACKLOG"))).thenReturn(1L);
        when(taskRepositoryMock.save(expectedTask)).thenReturn(expectedTask);

        doAnswer(invocation -> {
            mapperMock.map(taskRequest, expectedTask);
            expectedTask.setDescription(taskRequest.getDescription());
            expectedTask.setStatus(taskRequest.getStatus());
            expectedTask.setPosition(1L);
            expectedTask.setImageUrl("https://example.com/sample.txt");
            expectedTask.setBoardRef("TAG");
            expectedTask.setCreatedBy(user);
            expectedTask.setBoard(board);
            return expectedTask;
        }).when(taskRepositoryMock).save(expectedTask);

        // verify method throws an exception
        assertThrows(ResourceNotFoundException.class, () -> taskServiceMock.createTask("TAG", "test@test.com", taskRequest));

        // verify repository method calls
        verify(boardRepositoryMock, times(1)).findByBoardTag("TAG");
    }

    @Test
    void testMoveTaskThrowsResourceNotFoundExceptionWhenBoardTagNotFound() {
        // Setting up variables
        String boardRef = "board-1";
        String status = "new";
        Long position = 1L;

        // set up repository mocks
        when(taskRepositoryMock.findByBoardRef(boardRef)).thenReturn(Optional.empty());
        // verify method throws an exception
        assertThrows(ResourceNotFoundException.class, () -> taskServiceMock.moveTask(boardRef, status, position));
        // verify repository method calls
        verify(taskRepositoryMock, times(1)).findByBoardRef(boardRef);
    }

    @Test
    void testMoveTaskSameStatusDoesNotChangeTaskStatus() throws ResourceNotFoundException {
        Board board = new Board();
        board.setBoardTag("TAG");
        String status = "FINAL";

        //Setting up simple Tasks
        Task task = new Task();
        task.setId(1L);
        task.setBoardRef(boardRef);
        task.setStatus(status);
        task.setPosition(2L);
        task.setBoard(board);

        Task task1 = new Task();
        task1.setId(2L);
        task1.setBoardRef(boardRef);
        task1.setStatus(status);
        task1.setPosition(1L);
        task.setBoard(board);

        Task task2 = new Task();
        task2.setId(3L);
        task2.setBoardRef(boardRef);
        task2.setStatus(status);
        task2.setPosition(3L);
        task.setBoard(board);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        // set up repository mocks
        when(taskRepositoryMock.findByBoardRef(boardRef)).thenReturn(Optional.of(task));
        when(taskRepositoryMock.findAllByBoardAndStatus(task.getBoard(), status)).thenReturn(tasks);
        when(taskRepositoryMock.save(task)).thenReturn(task);

        // move tasks position
        Task result = taskServiceMock.moveTask(boardRef, status, 3L);

        // verify repository method calls
        verify(taskRepositoryMock, times(1)).save(task);
        verify(taskRepositoryMock, times(1)).findByBoardRef(boardRef);
        verify(taskRepositoryMock, times(1)).findAllByBoardAndStatus(task.getBoard(), status);

        // verify task positions have been updated correctly
        assertEquals(2L, result.getPosition());
        assertEquals("FINAL", result.getStatus());
    }

    @Test
    public void testMoveTaskToDifferentStatusChangesTaskStatus() throws ResourceNotFoundException {
        Board board = new Board();
        board.setBoardTag("TAG");

        // create sample tasks
        Task task = new Task();
        task.setId(1L);
        task.setBoardRef(boardRef);
        task.setStatus("BACKLOG");
        task.setPosition(1L);
        task.setBoard(board);

        // setting up repository mocks
        when(taskRepositoryMock.findByBoardRef(boardRef)).thenReturn(Optional.of(task));
        when(taskRepositoryMock.findByBoardRef(eq(boardRef))).thenReturn(Optional.of(task));
        when(taskRepositoryMock.findAllByBoardAndStatus(eq(board), eq("DONE"))).thenReturn(new ArrayList<>());

        when(taskRepositoryMock.save(any())).thenReturn(task);

        // move task to a new position
        Task movedTask = taskServiceMock.moveTask(boardRef, "DONE", 1L);

        // verify task positions have been updated correctly
        assertEquals("DONE", movedTask.getStatus());
        assertEquals(0L, task.getPosition().longValue());

        // verify repository method calls
        verify(taskRepositoryMock).findByBoardRef(eq(boardRef));
        verify(taskRepositoryMock).findAllByBoardAndStatus(eq(board), eq("DONE"));
        verify(taskRepositoryMock, times(1)).save(any());
    }

    @Test
    public void editStatus_shouldEditTasksStatus() throws ResourceNotFoundException {
        Board board = new Board();
        board.setBoardTag("TAG");
        String status = "DONE";
        List<Task> tasks = new ArrayList<>();

        //Setting up simple Tasks
        Task task1 = new Task();
        task1.setBoardRef("TAG-1");
        task1.setStatus(status);
        task1.setPosition(1L);

        Task task2 = new Task();
        task2.setBoardRef("TAG-2");
        task2.setStatus(status);
        task2.setPosition(3L);

        tasks.add(task1);
        tasks.add(task2);

        // setting up repository mocks
        when(taskRepositoryMock.saveAll(anyList())).thenReturn(tasks);
        when(boardRepositoryMock.findByBoardTag("TAG")).thenReturn(Optional.of(board));
        when(taskRepositoryMock.findAllByBoardAndStatus(eq(board), eq("DONE"))).thenReturn(tasks);

        StatusRequest request = new StatusRequest();
        request.setPreviousStatus(status);
        request.setCurrentStatus("Final");

        String editedStatus = Helper.formatStatus(request.getCurrentStatus());
        // Act
        List<Task> result = taskServiceMock.editStatus(request, "TAG");

        // verify repository method calls
        verify(taskRepositoryMock, times(1)).saveAll(anyList());
        verify(boardRepositoryMock, times(1)).findByBoardTag("TAG");
        verify(taskRepositoryMock, times(1)).findAllByBoardAndStatus(board, status);

        //Assert Results has been updated
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(editedStatus, result.get(0).getStatus());
        assertEquals(editedStatus, result.get(1).getStatus());
    }

    @Test
    public void editStatus_shouldThrowResourceNotFoundException() {
        Board board = new Board();
        board.setBoardTag("TAG");

        String status = "DONE";

        // setting up repository mocks
        when(taskRepositoryMock.findByBoardRef(anyString())).thenReturn(Optional.empty());

        StatusRequest request = new StatusRequest();
        request.setCurrentStatus(status);

        when(boardRepositoryMock.findByBoardTag("TAG")).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> taskServiceMock.editStatus(request, "TAG"));

        // verify repository method calls
        verify(boardRepositoryMock, times(1)).findByBoardTag("TAG");
    }

    @Test
    public void deleteStatus_shouldDeleteTaskStatusAndChangeTaskNameToNoStatus() throws ResourceNotFoundException {
        Board board = new Board();
        board.setBoardTag("TAG");
        String status = "DONE";

        List<Task> tasks = new ArrayList<>();
        //Setting up simple Tasks
        Task task1 = new Task();
        task1.setBoardRef("TAG-1");
        task1.setStatus(status);
        task1.setPosition(1L);

        Task task2 = new Task();
        task2.setBoardRef("TAG-2");
        task2.setStatus(status);
        task2.setPosition(3L);

        tasks.add(task1);
        tasks.add(task2);

        // setting up repository mocks
        when(taskRepositoryMock.saveAll(anyList())).thenReturn(tasks);
        when(boardRepositoryMock.findByBoardTag("TAG")).thenReturn(Optional.of(board));
        when(taskRepositoryMock.findAllByBoardAndStatus(eq(board), eq("DONE"))).thenReturn(tasks);


        StatusRequest request = new StatusRequest();
        request.setPreviousStatus(status);

        // Act
        List<Task> result = taskServiceMock.deleteStatus(request, "TAG");
        String editedStatus = Helper.formatStatus(request.getCurrentStatus());

        // verify repository method calls
        verify(taskRepositoryMock, times(1)).saveAll(anyList());
        verify(boardRepositoryMock, times(1)).findByBoardTag("TAG");
        verify(taskRepositoryMock, times(1)).findAllByBoardAndStatus(board, status);

        //Assert Results has been updated
        assertEquals(editedStatus, task1.getStatus());
        assertEquals(editedStatus, task2.getStatus());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(editedStatus, result.get(0).getStatus());
        assertEquals(editedStatus, result.get(1).getStatus());
    }
}