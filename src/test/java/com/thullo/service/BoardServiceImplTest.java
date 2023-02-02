package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.repository.BoardRepository;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    private ModelMapper mapper;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board board;
    private BoardRequest boardRequest;

    private BoardResponse boardResponse;
    String boardName = "DevDegree challenge";
    String imageUrl = "http://localhost:8080/api/v1/thullo/files/123e4567-e89b-12d3-a456-426655440000";

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setName(boardName);

        boardRequest = new BoardRequest();
        boardRequest.setName(boardName);

        boardResponse = new BoardResponse();
        boardResponse.setName(boardName);
    }

    @Test
    void testCreateBoard_withBoardName_createANewBoard(){
            when(mapper.map(boardRequest, Board.class))
                    .thenReturn(board);
        when(mapper.map(board, BoardResponse.class))
                .thenReturn(boardResponse);
            when(boardRepository.save(board)).thenReturn(board);

        BoardResponse actualResponse = boardService.createBoard(boardRequest);

        verify(mapper).map(boardRequest, Board.class);
        verify(boardRepository).save(board);
        assertEquals(boardName, actualResponse.getName());
    }

    @Test
    void testCreateBoard_WithBoardNameAndCoverImage_createANewBoard() throws IOException {
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/code.png");
        boardRequest.setFile(multipartFile);
        boardResponse.setImageUrl(imageUrl);

        when(mapper.map(boardRequest, Board.class))
                .thenReturn(board);

        when(fileService.uploadFile(multipartFile, ""))
                .thenReturn(imageUrl);

        when(boardRepository.save(board)).thenReturn(board);

        when(mapper.map(board, BoardResponse.class))
                .thenReturn(boardResponse);



        BoardResponse actualResponse = boardService.createBoard(boardRequest);

        verify(mapper).map(boardRequest, Board.class);
        verify(boardRepository).save(board);
        verify(fileService).uploadFile(multipartFile, "");
        assertEquals(boardName, actualResponse.getName());
        assertEquals(imageUrl, actualResponse.getImageUrl());
    }


    public MultipartFile getMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    }
}