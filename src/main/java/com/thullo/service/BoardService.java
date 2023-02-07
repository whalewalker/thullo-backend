package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    /**
     * Creates a new board based on the provided board request.
     *
     * @param boardRequest The request containing the information for the new board to be created.
     * @return A response object containing the result of the board creation process.
     */
    BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal principal) throws UserException, BadRequestException, IOException;

    Board getBoard(Long id) throws BadRequestException;

    List<Board> getBoards(String email) throws UserException;
}
