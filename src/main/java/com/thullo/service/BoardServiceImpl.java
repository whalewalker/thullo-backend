package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper mapper;

    private final FileService fileService;

    private final UserRepository userRepository;

    /**
     * Creates a new board based on the provided board request.
     *
     * @param boardRequest The request containing the information for the new board to be created.
     * @return A response object containing the result of the board creation process.
     */
    public BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal principal) throws UserException {
        User user = internalFindUserByEmail(principal.getEmail());
        Board board = mapper.map(boardRequest, Board.class);
        board.setUser(user);
        String imageUrl = null;
        if (boardRequest.getFile() != null){
            imageUrl = fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl());
        }
        board.setImageUrl(imageUrl);
        createDefaultTaskColumn(board);
        boardRepository.save(board);
        return mapper.map(board, BoardResponse.class);
    }

    @Override
    public Board getBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(()-> new BadRequestException("Board not found!"));
    }

    @Override
    public List<Board> getBoards(String  email) throws UserException {
        User user = internalFindUserByEmail(email);
        return boardRepository.getAllByUser(user);
    }

    public boolean isBoardOwner(Long boardId, String email) {
        Board board = getBoard(boardId);
        return board.getUser().getEmail().equals(email);
    }

    private void createDefaultTaskColumn(Board board) {
        board.setTaskColumns(List.of(
                new TaskColumn("Backlog \uD83E\uDD14", board),
                new TaskColumn("In Progress \uD83D\uDCDA", board),
                new TaskColumn("In Review ⚙️", board),
                new TaskColumn("Completed \uD83D\uDE4C\uD83C\uDFFD", board))
        );
    }

    private User internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(format("user not found with email %s", email)));
    }
}
