package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
import com.thullo.security.UserPrincipal;
import com.thullo.service.BoardService;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/thullo/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private static final String MESSAGE = "message";

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse> createBoard(@Valid BoardRequest boardRequest,
                                                   @CurrentUser UserPrincipal principal,
                                                   HttpServletRequest request) {
        boardRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            BoardResponse board = boardService.createBoard(boardRequest, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully created", board));

        } catch (UserException | IOException | BadRequestException | ThulloException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(),
                    new HashMap<>(Map.of(MESSAGE, ex.getMessage()))));
        }
    }

    @PutMapping(value = "/{boardTag}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> updateBoard(@PathVariable String boardTag,
                                                   BoardRequest boardRequest,
                                                   HttpServletRequest request) {

        boardRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            BoardResponse boardResponse = boardService.updateBoard(boardRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully updated", boardResponse));
        } catch (UserException | IOException | BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(),
                    new HashMap<>(Map.of(MESSAGE, ex.getMessage()))));
        }
    }

    @DeleteMapping("/{boardTag}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> deleteBoard(@PathVariable String boardTag) {
        try {
            boardService.deleteBoard(boardTag);
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully delete"));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @GetMapping("/{boardTag}")
    public ResponseEntity<ApiResponse> getABoard(@PathVariable String boardTag) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully fetched",
                    boardService.getBoard(boardTag)));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(),
                    new HashMap<>(Map.of(MESSAGE, ex.getMessage()))));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getBoards(@CurrentUser UserPrincipal userPrincipal,
                                                 @RequestParam Map<String, String> filterParams) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully fetched",
                    boardService.getBoards(userPrincipal, filterParams)));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("/{boardTag}/collaborator/{collaboratorEmail}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> addCollaborator(@PathVariable String boardTag, @PathVariable String collaboratorEmail) {
        try {
            boardService.addACollaborator(boardTag, collaboratorEmail);
            return ResponseEntity.ok(new ApiResponse(true, "collaborators successfully added"));
        } catch (BadRequestException | UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PostMapping("/{boardTag}/remove/collaborator/{collaboratorEmail}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> removeCollaborator(@PathVariable String boardTag, @PathVariable String collaboratorEmail) {
        try {
            boardService.removeACollaborator(boardTag, collaboratorEmail);
            return ResponseEntity.ok(new ApiResponse(true, "collaborators successfully added"));
        } catch (BadRequestException | UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}