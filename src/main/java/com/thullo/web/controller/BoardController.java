package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
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

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/create-board")
    public ResponseEntity<ApiResponse> createBoard(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("boardName") String boardName,
                                                   @CurrentUser UserPrincipal principal, HttpServletRequest request) {
        try {
            BoardRequest boardRequest = new BoardRequest(boardName, request.getRequestURL().toString(), file);
            BoardResponse board = boardService.createBoard(boardRequest, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully created", board));
        } catch (UserException | IOException | BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Bad request, check your request data",
                    new HashMap<>(Map.of("message", ex.getMessage()))));
        }
    }

    @GetMapping("/boards/{boardId}")
    @PreAuthorize("@boardServiceImpl.isBoardOwner(#boardId, authentication.principal.email)")
    public ResponseEntity<ApiResponse> getABoard(@PathVariable("boardId") Long boardId) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully fetched",
                    boardService.getBoard(boardId)));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Bad request, check your request data",
                    new HashMap<>(Map.of("message", ex.getMessage()))));
        }
    }

    @GetMapping("/boards")
    public ResponseEntity<ApiResponse> getBoards(@CurrentUser UserPrincipal userPrincipal) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully fetched",
                    boardService.getBoards(userPrincipal.getEmail())));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Bad request, check your request data"));
        }
    }
}
