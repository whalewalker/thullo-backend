package com.thullo.service;

import com.thullo.data.model.*;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.util.Helper;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

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

    public BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal userPrincipal) throws UserException, BadRequestException, IOException {

        Board board = mapper.map(boardRequest, Board.class);
        if (Helper.isNullOrEmpty(board.getName())) throw new BadRequestException("Board name cannot be empty");
        User user = findByEmail(userPrincipal.getEmail());

        BoardVisibility boardVisibility = BoardVisibility.getBoardVisibility(boardRequest.getBoardVisibility());
        board.setBoardVisibility(boardVisibility == null ? BoardVisibility.PRIVATE : boardVisibility);

        String imageUrl = null;
        board.setCreatedBy(user);
        if (boardRequest.getFile() != null) {
            imageUrl = fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl());
        }
        board.setImageUrl(imageUrl);
        board.setBoardTag(generateThreeLetterWord(boardRequest.getBoardName().toUpperCase()));

        return getBoard(boardRepository.save(board));
    }

    @Override
    public BoardResponse getBoard(String boardTag) throws BadRequestException {
        Board board = getBoardByTag(boardTag);
        if (board == null) throw new BadRequestException(BOARD_NOT_FOUND);
        return getBoard(board);
    }

    private BoardResponse getBoard(Board board) {
        return getBoardResponse(board);
    }

    public Board getBoardByTag(String boardTag) {
        return boardRepository.findByBoardTag(boardTag).orElse(null);
    }


    public BoardResponse getBoardResponse(Board board) {
        BoardResponse boardResponse = mapper.map(board, BoardResponse.class);
        List<Task> tasks = board.getTasks();
        Set<String> status = new LinkedHashSet<>(Arrays.asList("BACKLOG", "IN_PROGRESS", "IN_REVIEW", "COMPLETED"));

        Map<String, List<Task>> columns = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        if (columns.isEmpty()) {
            Arrays.stream(status.toArray()).forEach(columnName -> {
                BoardResponse.Column column = new BoardResponse.Column();
                column.setName(columnName.toString());
                column.setTasks(Collections.emptyList());
                boardResponse.getTaskColumn().add(column);
            });
        } else {
            Arrays.stream(status.toArray()).forEach(columnName -> {
                BoardResponse.Column column = new BoardResponse.Column();
                column.setName(columnName.toString());
                List<Task> columnTasks = columns.getOrDefault(columnName.toString(), Collections.emptyList());
                column.setTasks(columnTasks);
                boardResponse.getTaskColumn().add(column);
            });
        }
        return boardResponse;
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
    public Board updateBoard(BoardRequest boardRequest)
            throws BadRequestException, IOException {
        Board board = getBoardByTag(boardRequest.getBoardTag());
        if (board == null) throw new BadRequestException(BOARD_NOT_FOUND);

        String imageUrl;
        mapper.map(boardRequest, board);

        if (!Helper.isNullOrEmpty(boardRequest.getBoardVisibility())) {
            BoardVisibility visibility = BoardVisibility.getBoardVisibility(boardRequest.getBoardVisibility());
            board.setBoardVisibility(visibility == null ? board.getBoardVisibility() : visibility);
        }

        if (boardRequest.getFile() != null) {
            String fileId = Helper.extractFileIdFromUrl(board.getImageUrl());
            fileService.deleteFile(fileId);
            imageUrl = fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl());
            board.setImageUrl(imageUrl);
        }
        return boardRepository.save(board);
    }

    @Override
    public void addACollaborator(String boardTag, String collaboratorEmail) throws BadRequestException, UserException {
        String title = "You have been added as a collaborator on board: " + boardTag;
        String message = "You have been added as a collaborator on board " + boardTag;

        Board board = getBoardByTag(boardTag);
        if (board == null) throw new BadRequestException(BOARD_NOT_FOUND);
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
        if (board == null) throw new BadRequestException(BOARD_NOT_FOUND);
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

    private String generateThreeLetterWord(String boardName) {
        Set<String> usedThreeLetterWords = boardRepository.findAll().stream()
                .map(Board::getBoardTag)
                .collect(Collectors.toSet());

        for (int i = 0; i < boardName.length() - 2; i++) {
            String threeLetterWord = boardName.replace(" ", "").substring(i, i + 3);
            if (!usedThreeLetterWords.contains(threeLetterWord)) {
                return threeLetterWord;
            }
        }
        throw new IllegalStateException("All three-letter substrings have been used. Please choose a different board name.");
    }

    public boolean hasBoardRole(String boardOwner, String boardTag) {
        Board board = getBoardByTag(boardTag);
        if (board == null) return false;
        return board.getCreatedBy().getEmail().equals(boardOwner);
    }

    public boolean isBoardOwner(String collaboratorEmail, String boardOwnerEmail) {
        return collaboratorEmail.equals(boardOwnerEmail);
    }
}