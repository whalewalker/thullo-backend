package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.Task;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.payload.request.BoardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.thullo.data.model.BoardVisibility.PRIVATE;
import static com.thullo.data.model.BoardVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceImplTest {
    @Mock
    private BoardRepository boardRepositoryMock;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private BoardServiceImpl boardServiceImplMock;
    @Mock
    private UserRepository userRepositoryMock;
    private Board board;

    @BeforeEach
    void setUp() {

        board = new Board();
        board.setName("Test Board");
        board.setBoardVisibility(PUBLIC);
        board.setUser(new User());
        board.setTasks(Arrays.asList(
                new Task(), new Task()
        ));
    }

    @Test
    void testUpdateBoardWithValidRequest() throws BadRequestException, IOException {

        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setName("Test Board Update");
        boardRequest.setBoardVisibility("PRIVATE");
        boardRequest.setBoardTag("TES");

        when(userRepositoryMock.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));

        when(boardRepositoryMock.findByBoardTag("TES")).thenReturn(Optional.of(board));

        doAnswer(invocation -> {
            mapper.map(boardRequest, board);
            board.setName(boardRequest.getName()); // Update the board name before saving
            return board;
        }).when(boardRepositoryMock).save(board);

        when(boardRepositoryMock.save(board)).thenReturn(board);

        Board response = boardServiceImplMock.updateBoard(boardRequest);

        verify(boardRepositoryMock).findByBoardTag("TES");
        verify(mapper, times(2)).map(boardRequest, board);
        verify(boardRepositoryMock).save(board);

        assertNotNull(response);
        assertEquals("Test Board Update", response.getName());
        assertEquals(PRIVATE, response.getBoardVisibility());
    }
}