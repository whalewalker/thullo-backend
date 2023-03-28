package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.Task;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
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
import java.util.List;
import java.util.Optional;

import static com.thullo.data.model.BoardVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @Mock
    private UserPrincipal userPrincipalMock;
    private Board board;

    @BeforeEach
    void setUp() {
        userPrincipalMock = mock(UserPrincipal.class);

        board = new Board();
        board.setName("Test Board");
        board.setBoardVisibility(PUBLIC);
        board.setUser(new User());
        board.setTasks(Arrays.asList(
                new Task(), new Task()
        ));
    }

    @Test
    public void testUpdateBoardWithValidRequest() throws UserException, BadRequestException, IOException {

        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setName("Test Board Update");
        boardRequest.setBoardVisibility("PRIVATE");
        boardRequest.setBoardTag("TES");

        when(userPrincipalMock.getEmail()).thenReturn("admin@gmail.com");
        when(userRepositoryMock.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));

        when(boardRepositoryMock.findByBoardTag(any(String.class))).thenReturn(Optional.of(board));
        when(mapper.map(boardRequest, Board.class)).thenReturn(board);
        when(boardRepositoryMock.save(board)).thenReturn(board);


        BoardResponse response = boardServiceImplMock.updateBoard(boardRequest, userPrincipalMock);

        assertNotNull(response);
        assertEquals("Test Board Update", response.getName());
        assertEquals("PRIVATE", response.getBoardVisibility());
    }
}