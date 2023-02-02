package com.thullo.web.controller;

import com.thullo.security.CurrentUser;
import com.thullo.security.UserPrincipal;
import com.thullo.service.BoardService;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/")
@RequiredArgsConstructor
@CrossOrigin()
public class BoardController {
    private final BoardService boardService;

    @PostMapping( "/create-board")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("boardName") String boardName, @CurrentUser UserPrincipal principal,  HttpServletRequest request){
        BoardRequest boardRequest = new BoardRequest(boardName, request.getRequestURL().toString(),file);
        try {
            BoardResponse board = boardService.createBoard(boardRequest, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Board successfully created", board));
        }catch (UserException ex)  {
           return  ResponseEntity.badRequest().body(new ApiResponse(false, "Bad request, check your request data"));
        }
    }
}
