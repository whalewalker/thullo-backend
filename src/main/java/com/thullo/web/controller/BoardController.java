package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
import com.thullo.security.UserPrincipal;
import com.thullo.service.BoardService;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.ResponseDTO;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/create-board")
    public ResponseEntity<ResponseDTO> createBoard(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("boardName") String boardName, @CurrentUser UserPrincipal principal, HttpServletRequest request) {
        BoardRequest boardRequest = new BoardRequest(boardName, request.getRequestURL().toString(), file);
        try {
            BoardResponse board = boardService.createBoard(boardRequest, principal);
            return ResponseEntity.ok(new ResponseDTO(true, "Board successfully created", board));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Bad request, check your request data"));
        }
    }

    @GetMapping("/boards/{boardId}")
    @PreAuthorize("@boardServiceImpl.isBoardOwner(#boardId, authentication.principal.email)")
    public ResponseEntity<ResponseDTO> getABoard(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(new ResponseDTO(true, "Board successfully fetched",
                boardService.getBoard(boardId)));
    }

    @GetMapping("/boards")
    public ResponseEntity<ResponseDTO> getBoards(@CurrentUser UserPrincipal userPrincipal) {
        try {
            return ResponseEntity.ok(new ResponseDTO(true, "Board successfully fetched",
                    boardService.getBoards(userPrincipal.getEmail())));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Bad request, check your request data"));
        }
    }
}
