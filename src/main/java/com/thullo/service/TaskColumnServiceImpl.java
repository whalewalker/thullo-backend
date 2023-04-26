package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.Task;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.TaskColumnRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.TaskColumnRequest;
import com.thullo.web.payload.response.TaskColumnResponse;
import com.thullo.web.payload.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskColumnServiceImpl implements TaskColumnService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final TaskColumnRepository taskColumnRepository;
    private final ModelMapper mapper;

    private TaskColumn findTaskColumnById(Long columnId) throws ResourceNotFoundException {
        return taskColumnRepository.findById(columnId).orElseThrow(
                () -> new ResourceNotFoundException("Task column not found"));
    }

    @Override
    public TaskColumnResponse createTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag, UserPrincipal principal) throws UserException, ThulloException, BadRequestException {
        User user = findByEmail(principal.getEmail());
        Board board = getBoardByTag(boardTag);
        checkTaskColumnExists(taskColumnRequest.getName(), user);
        TaskColumn taskColumn = mapper.map(taskColumnRequest, TaskColumn.class);
        taskColumn.setCreatedBy(user);
        taskColumn.setBoard(board);
        TaskColumn savedTaskColumn = saveTaskColumn(taskColumn);
        return getTaskColumnResponse(savedTaskColumn);
    }

    private void checkTaskColumnExists(String name, User user) throws ThulloException {
        if (taskColumnRepository.existsByNameAndCreatedBy(name, user)) {
            throw new ThulloException(String.format("Task column with name '%s' already exists", name));
        }
    }

    @Override
    public TaskColumnResponse editTaskColumn(TaskColumnRequest taskColumnRequest, UserPrincipal principal) throws ResourceNotFoundException, UserException, ThulloException {
        User user = findByEmail(principal.getEmail());
        checkTaskColumnExists(taskColumnRequest.getName(), user);
        TaskColumn taskColumnToUpdate = findTaskColumnById(taskColumnRequest.getTaskColumnId());
        mapper.map(taskColumnRequest, taskColumnToUpdate);
        TaskColumn savedTaskColumn = saveTaskColumn(taskColumnToUpdate);
        return getTaskColumnResponse(savedTaskColumn);
    }

    @Override
    public void deleteTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag, UserPrincipal userPrincipal) throws ResourceNotFoundException, ThulloException, UserException, BadRequestException {
        TaskColumn taskColumnToDelete = findTaskColumnById(taskColumnRequest.getTaskColumnId());
        Board board = getBoardByTag(boardTag);

        if (taskColumnToDelete.getTasks().isEmpty()) {
            deleteTaskColumn(taskColumnToDelete, board);
            return;
        }

        if (taskColumnToDelete.getName().equals("No status")) {
            if (taskColumnToDelete.getTasks().isEmpty()) {
                deleteTaskColumn(taskColumnToDelete, board);
            } else {
                throw new BadRequestException("Cannot delete task column 'No status' as it contains at least one task.");
            }
        } else {
            Optional<TaskColumn> noStatusTaskColumnOptional = board.getTaskColumns().stream()
                    .filter(taskColumn -> taskColumn.getName().equals("No status"))
                    .findFirst();

            if (noStatusTaskColumnOptional.isPresent()) {
                TaskColumn noStatusTaskColumn = noStatusTaskColumnOptional.get();
                taskColumnToDelete.getTasks().forEach(task -> task.setTaskColumn(noStatusTaskColumn));
                taskColumnRepository.save(noStatusTaskColumn);
                deleteTaskColumn(taskColumnToDelete, board);
            } else {
                TaskColumnRequest columnRequest = new TaskColumnRequest("No status", taskColumnToDelete.getId());
                createTaskColumn(columnRequest, boardTag, userPrincipal);
                deleteTaskColumn(taskColumnToDelete, board);
            }
        }
    }


    private void deleteTaskColumn(TaskColumn taskColumnToDelete, Board board) {
        board.getTaskColumns().remove(taskColumnToDelete);
        taskColumnRepository.delete(taskColumnToDelete);
        boardRepository.save(board);
    }


    private TaskColumnResponse getTaskColumnResponse(TaskColumn taskColumn) {
        TaskColumnResponse taskColumnResponse = mapper.map(taskColumn, TaskColumnResponse.class);
        List<TaskResponse> taskResponses = getTaskResponses(taskColumn.getTasks());
        taskColumnResponse.setTasks(taskResponses);
        return taskColumnResponse;
    }

    private List<TaskResponse> getTaskResponses(List<Task> tasks) {
        return tasks.stream().map(task -> mapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    private TaskColumn saveTaskColumn(TaskColumn taskColumn) {
        return taskColumnRepository.save(taskColumn);
    }


    private User findByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(format("user not found with email '%s'", email)));
    }

    public Board getBoardByTag(String boardTag) throws BadRequestException {
        return boardRepository.findByBoardTag(boardTag).orElseThrow(() -> new BadRequestException(format("Board  with tag '%s' is not found", boardTag)));
    }
}
