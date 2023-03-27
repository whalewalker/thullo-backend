package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
import com.thullo.data.model.BoardVisibility;
import com.thullo.security.UserPrincipal;
import com.thullo.service.BoardService;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.thullo.data.model.BoardVisibility.PRIVATE;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    @PostMapping
    public ResponseEntity<ApiResponse> createBoard(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("boardName") String boardName,
                                                   @RequestParam("visibilityStatus") String visibilityStatus, @CurrentUser UserPrincipal principal, HttpServletRequest request) {
        try {
            BoardRequest boardRequest = new BoardRequest(boardName, request.getRequestURL().toString(), file, visibilityStatus);
            BoardResponse board = boardService.createBoard(boardRequest, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully created", board));
        } catch (UserException | IOException | BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(),
                    new HashMap<>(Map.of("message", ex.getMessage()))));
        }
    }

    @GetMapping("/{boardTag}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> getABoard(@PathVariable String boardTag) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully fetched",
                    boardService.getBoard(boardTag)));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(),
                    new HashMap<>(Map.of("message", ex.getMessage()))));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getBoards(@CurrentUser UserPrincipal userPrincipal) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully fetched",
                    boardService.getBoards(userPrincipal)));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("/{boardTag}/collaborators")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> addCollaboratorToBoard(@PathVariable String boardTag, @RequestBody Set<String> collaborators) {
        try {
            boardService.addCollaboratorToBoard(boardTag, collaborators);
            return ResponseEntity.ok(new ApiResponse(true, "collaborators successfully added"));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PostMapping("/{boardTag}/remove/collaborators")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> removeCollaboratorToBoard(@PathVariable String boardTag, @RequestBody Set<String> collaborators) {
        try {
            boardService.removeCollaboratorsFromBoard(boardTag, collaborators);
            return ResponseEntity.ok(new ApiResponse(true, "collaborators successfully added"));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}
