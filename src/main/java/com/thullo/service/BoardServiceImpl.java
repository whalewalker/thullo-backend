package com.thullo.service;

import com.thullo.data.model.*;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.TaskColumnRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.util.Helper;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thullo.data.model.BoardVisibility.getBoardVisibility;
import static com.thullo.util.Helper.getBoardResponseDetails;
import static com.thullo.util.Helper.isNullOrEmpty;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final TaskColumnRepository taskColumnRepository;
    private final TaskRepository taskRepository;

    private final BoardRepository boardRepository;
    private final ModelMapper mapper;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final RoleServiceImpl roleService;
    private final NotificationService notificationService;
    private static final String BOARD_NOT_FOUND = "Board not found";


    /**
     * Creates a new board based on the provided board request.
     *
     * @param boardRequest The request containing the information for the new board to be created.
     * @return A response object containing the result of the board creation process.
     */

    public BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal userPrincipal)
            throws UserException, BadRequestException, IOException, ThulloException {

        if (isNullOrEmpty(boardRequest.getName()))
            throw new BadRequestException("Board name cannot be empty");

        User user = findByEmail(userPrincipal.getEmail());
        checkBoardNameExists(boardRequest.getName(), user);

        Board board = mapper.map(boardRequest, Board.class);
        BoardVisibility boardVisibility = getBoardVisibility(boardRequest.getBoardVisibility());
        board.setBoardVisibility(boardVisibility == null ? BoardVisibility.PRIVATE : boardVisibility);

        board.setCreatedBy(user);
        String imageUrl = uploadBoardFile(boardRequest.getFile(), boardRequest.getRequestUrl());
        board.setImageUrl(imageUrl);

        createDefaultTaskColumn(board, user);
        board.setBoardTag(generateThreeLetterWord(boardRequest.getName().toUpperCase(), user.getEmail()));

        Board savedBoard = boardRepository.save(board);
        return getBoardResponse(savedBoard);
    }

    private void checkBoardNameExists(String boardName, User user) throws ThulloException {
        if (boardRepository.existsByNameAndCreatedBy(boardName, user))
            throw new ThulloException(format("Board with name '%s' already exists", boardName));
    }

    private String uploadBoardFile(MultipartFile file, String requestUrl) throws IOException, BadRequestException {
        return file != null ? fileService.uploadFile(file, requestUrl) : null;
    }

    @Override
    public BoardResponse getBoard(String boardTag) throws BadRequestException {
        Board board = getBoardByTag(boardTag);
        return getBoardResponse(board);
    }


    public Board getBoardByTag(String boardTag) throws BadRequestException {
        return boardRepository.findByBoardTag(boardTag).orElseThrow(() -> new BadRequestException(BOARD_NOT_FOUND));
    }

    private void createDefaultTaskColumn(Board board, User user) {
        board.setTaskColumns(List.of(
                new TaskColumn("Backlog \uD83E\uDD14", board, user),
                new TaskColumn("In Progress \uD83D\uDCDA", board, user),
                new TaskColumn("In Review ⚙️", board, user),
                new TaskColumn("Completed \uD83D\uDE4C\uD83C\uDFFD", board, user))
        );
    }


    public List<BoardResponse> getBoards(UserPrincipal userPrincipal, Map<String, String> filterParams) {
        List<Board> filteredBoards = new ArrayList<>();

        if (filterParams.isEmpty()) {
            filteredBoards = boardRepository.findAllByCreatedBy(userPrincipal.getEmail());
            filteredBoards.addAll(boardRepository.findAllByContributors(userPrincipal.getEmail()));
            filteredBoards.addAll(boardRepository.findAllByCollaborators(userPrincipal.getEmail()));

        } else if (filterParams.containsKey("contributor")) {
            filteredBoards = boardRepository.findAllByContributors(userPrincipal.getEmail());
        } else if (filterParams.containsKey("collaborator")) {
            filteredBoards = boardRepository.findAllByCollaborators(userPrincipal.getEmail());
        }

        List<BoardResponse> boardResponses = new ArrayList<>();
        filteredBoards.forEach(board -> boardResponses.add(getBoardResponse(board)));
        return boardResponses;
    }

    @Override
    public BoardResponse updateBoard(BoardRequest boardRequest) throws BadRequestException, IOException {
        Board board = getBoardByTag(boardRequest.getBoardTag());
        mapper.map(boardRequest, board);

        updateBoardVisibility(board, boardRequest);
        updateBoardImage(board, boardRequest);

        Board updatedBoard = boardRepository.save(board);
        return mapper.map(updatedBoard, BoardResponse.class);
    }

    @Override
    public BoardResponse getBoardResponse(Board board) {
        return getBoardResponseDetails(board, mapper);
    }

    private void updateBoardVisibility(Board board, BoardRequest boardRequest) {
        String boardVisibility = boardRequest.getBoardVisibility();
        if (!isNullOrEmpty(boardVisibility)) {
            BoardVisibility visibility = BoardVisibility.getBoardVisibility(boardVisibility);
            board.setBoardVisibility(visibility != null ? visibility : board.getBoardVisibility());
        }
    }

    private void updateBoardImage(Board board, BoardRequest boardRequest) throws IOException, BadRequestException {
        if (boardRequest.getFile() != null) {
            String imageUrl = board.getImageUrl();
            if (imageUrl != null) {
                String fileId = Helper.extractFileIdFromUrl(imageUrl);
                fileService.deleteFile(fileId);
            }

            String newImageUrl = fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl());
            board.setImageUrl(newImageUrl);
        }
    }


    @Override
    public void addACollaborator(String boardTag, String collaboratorEmail) throws BadRequestException, UserException {
        String title = "You have been added as a collaborator on board: " + boardTag;
        String message = "You have been added as a collaborator on board " + boardTag;

        Board board = getBoardByTag(boardTag);
        if (isBoardOwner(collaboratorEmail, board.getCreatedBy().getEmail()))
            throw new BadRequestException("Creator of a board cannot be added a collaborator");
        Set<User> existingCollaborators = board.getCollaborators();
        User newCollaborator = findByEmail(collaboratorEmail);
        if (!existingCollaborators.contains(newCollaborator)) {
            existingCollaborators.add(newCollaborator);
            roleService.addTaskRoleToUser(newCollaborator, board);
            notificationService.sendNotificationToUser(newCollaborator, message, title, NotificationType.ADDED_AS_COLLABORATOR);
        }
    }


    @Override
    public void removeACollaborator(String boardTag, String collaboratorEmail) throws BadRequestException, UserException {
        String title = "You have been removed as a collaborator on board: " + boardTag;
        String message = "You have been removed as a collaborator on board " + boardTag;

        Board board = getBoardByTag(boardTag);
        if (isBoardOwner(collaboratorEmail, board.getCreatedBy().getEmail()))
            throw new BadRequestException("Creator of a board cannot be removed from board collaborators");
        Set<User> existingCollaborators = board.getCollaborators();
        User userToRemove = findByEmail(collaboratorEmail);
        if (existingCollaborators.contains(userToRemove)) {
            existingCollaborators.remove(userToRemove);
            roleService.removeBoardRoleFromUser(userToRemove, board);
            notificationService.sendNotificationToUser(userToRemove, message, title, NotificationType.REMOVED_AS_COLLABORATOR);
        }
    }

    private User findByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(format("user not found with email %s", email)));
    }

    private String generateThreeLetterWord(String boardName, String email) throws ThulloException {
        Set<String> usedThreeLetterWords = boardRepository.findAllByCreatedBy(email).stream()
                .map(Board::getBoardTag)
                .collect(Collectors.toSet());

        for (int i = 0; i < boardName.length() - 2; i++) {
            String threeLetterWord = boardName.replace(" ", "").substring(i, i + 3);
            if (!usedThreeLetterWords.contains(threeLetterWord)) {
                return threeLetterWord;
            }
        }
        throw new ThulloException("All three-letter substrings have been used. Please choose a different board name.");
    }

    public boolean hasBoardRole(String boardOwner, String boardTag) {
        Board board = boardRepository.findByBoardTag(boardTag).orElse(null);
        if (board == null) return false;
        return board.getCreatedBy().getEmail().equals(boardOwner);
    }

    public boolean isBoardOwner(String collaboratorEmail, String boardOwnerEmail) {
        return collaboratorEmail.equals(boardOwnerEmail);
    }

    @Override
    public void deleteBoard(String boardTag) throws BadRequestException {
        Board board = getBoardByTag(boardTag);
        board.getTaskColumns().forEach(taskColumn -> taskRepository.deleteAll(taskColumn.getTasks()));
        taskColumnRepository.deleteAll(board.getTaskColumns());
        boardRepository.delete(board);
    }
}