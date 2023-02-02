package com.thullo.service;

import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;

public interface BoardService {
    /**
     * Creates a new board based on the provided board request.
     *
     * @param boardRequest The request containing the information for the new board to be created.
     * @return A response object containing the result of the board creation process.
     */
    BoardResponse createBoard(BoardRequest boardRequest);
}
