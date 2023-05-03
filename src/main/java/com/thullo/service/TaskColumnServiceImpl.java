package com.thullo.service;

import com.thullo.data.model.Board;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.thullo.util.AppConstants.NO_STATUS;
import static com.thullo.util.Helper.getTaskColumnResponse;
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
        TaskColumn taskColumn = createTaskColumn(taskColumnRequest, boardTag, principal.getEmail());
        TaskColumn savedTaskColumn = saveTaskColumn(taskColumn);
        return getTaskColumnResponse(savedTaskColumn, mapper);
    }

    private TaskColumn createTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag, String email) throws UserException, BadRequestException, ThulloException {
        User user = findByEmail(email);
        Board board = getBoardByTag(boardTag);
        checkTaskColumnExists(taskColumnRequest.getName(), board);
        TaskColumn taskColumn = mapper.map(taskColumnRequest, TaskColumn.class);
        taskColumn.setCreatedBy(user);
        taskColumn.setBoard(board);
        return taskColumn;
    }

    private void checkTaskColumnExists(String name, Board board) throws ThulloException {
        if (taskColumnRepository.existsByNameAndBoard(name, board)) {
            throw new ThulloException(String.format("Task column with name '%s' already exists", name));
        }
    }

    @Override
    public TaskColumnResponse editTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag) throws ResourceNotFoundException, ThulloException, BadRequestException {
        Board board = getBoardByTag(boardTag);
        checkTaskColumnExists(taskColumnRequest.getName(), board);
        TaskColumn taskColumnToUpdate = findTaskColumnById(taskColumnRequest.getTaskColumnId());
        mapper.map(taskColumnRequest, taskColumnToUpdate);
        TaskColumn savedTaskColumn = saveTaskColumn(taskColumnToUpdate);
        return getTaskColumnResponse(savedTaskColumn, mapper);
    }


    @Override
    public TaskColumnResponse getTaskColumn(Map<String, String> params, String boardTag) throws BadRequestException {
        Board board = getBoardByTag(boardTag);
        Long taskColumnId = params.containsKey("taskColumnId") ? Long.parseLong(params.get("taskColumnId")) : null;
        String name = params.getOrDefault("name", null);
        Optional<TaskColumn> taskColumnOptional = taskColumnRepository.findTaskByParams(taskColumnId, name, board);
        TaskColumn taskColumn = taskColumnOptional.orElse(null);
        return taskColumn != null ? getTaskColumnResponse(taskColumn, mapper) : null;
    }


    @Override
    public void deleteTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag, UserPrincipal userPrincipal) throws ResourceNotFoundException, ThulloException, UserException, BadRequestException {
        TaskColumn taskColumnToDelete = findTaskColumnById(taskColumnRequest.getTaskColumnId());
        Board board = getBoardByTag(boardTag);

        if (taskColumnToDelete.getTasks().isEmpty()) {
            deleteTaskColumn(taskColumnToDelete, board);
            return;
        }

        if (taskColumnToDelete.getName().equals(NO_STATUS)) {
            if (taskColumnToDelete.getTasks().isEmpty()) {
                deleteTaskColumn(taskColumnToDelete, board);
            } else {
                throw new BadRequestException("Cannot delete task column 'No status' as it contains at least one task.");
            }
        } else {
            Optional<TaskColumn> noStatusTaskColumnOptional = board.getTaskColumns().stream()
                    .filter(taskColumn -> taskColumn.getName().equals(NO_STATUS))
                    .findFirst();

            if (noStatusTaskColumnOptional.isPresent()) {
                TaskColumn noStatusTaskColumn = noStatusTaskColumnOptional.get();
                taskColumnToDelete.getTasks().forEach(task -> task.setTaskColumn(noStatusTaskColumn));
                taskColumnRepository.save(noStatusTaskColumn);
                deleteTaskColumn(taskColumnToDelete, board);
            } else {
                TaskColumnRequest columnRequest = new TaskColumnRequest(NO_STATUS, taskColumnToDelete.getId());
                TaskColumn taskColumn = createTaskColumn(columnRequest, boardTag, userPrincipal.getEmail());
                taskColumnToDelete.getTasks().forEach(task -> task.setTaskColumn(taskColumn));
                taskColumnRepository.save(taskColumn);
                deleteTaskColumn(taskColumnToDelete, board);
            }
        }
    }

    private void deleteTaskColumn(TaskColumn taskColumnToDelete, Board board) {
        board.getTaskColumns().remove(taskColumnToDelete);
        taskColumnRepository.delete(taskColumnToDelete);
        boardRepository.save(board);
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
