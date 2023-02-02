package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
    @Override
    public BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal principal) throws UserException {
        User user = internalFindUserByEmail(principal.getEmail());
        Board board = mapper.map(boardRequest, Board.class);
        board.setUser(user);
        String imageUrl = fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl());
        board.setImageUrl(imageUrl);
        Board saveBoard = boardRepository.save(board);
        return mapper.map(saveBoard, BoardResponse.class);
    }

    private User internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(()-> new UserException(format( "user not found with email %s", email)));
    }
}
