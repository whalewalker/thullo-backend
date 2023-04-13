package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface BoardService {
    /**
     * Creates a new board based on the provided board request.
     *
     * @param boardRequest The request containing the information for the new board to be created.
     * @return A response object containing the result of the board creation process.
     */
    BoardResponse createBoard(BoardRequest boardRequest, UserPrincipal principal) throws UserException, BadRequestException, IOException;

    Board updateBoard(BoardRequest boardRequest)
            throws UserException, BadRequestException, IOException;

    BoardResponse getBoardResponse(Board board);

    BoardResponse getBoard(String boardTag) throws BadRequestException;

    List<BoardResponse> getBoards(UserPrincipal userPrincipal) throws UserException;

    void addCollaboratorToBoard(String boardTag, Set<String> collaborators) throws BadRequestException;

    void removeCollaboratorsFromBoard(String boardTag, Set<String> emails) throws BadRequestException;

    List<BoardResponse> categorizeBoardByCollaborator(UserPrincipal userPrincipal, Pageable pageable) throws UserException;
}
