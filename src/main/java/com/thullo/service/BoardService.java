package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BoardService {
    BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal principal) throws UserException, BadRequestException, IOException, ThulloException;

    BoardResponse updateBoard(BoardRequest boardRequest) throws UserException, BadRequestException, IOException;

    BoardResponse getBoardResponse(Board board);

    BoardResponse getBoard(String boardTag) throws BadRequestException;

    List<BoardResponse> getBoards(UserPrincipal userPrincipal, Map<String, String> filterParams) throws UserException;

    void addACollaborator(String boardTag, String collaboratorEmail) throws BadRequestException, UserException;

    void removeACollaborator(String boardTag, String collaboratorEmail) throws BadRequestException, UserException;
}
