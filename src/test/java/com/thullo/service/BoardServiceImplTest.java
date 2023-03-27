package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.request.UpdateBoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
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
    }

    @Test
    public void testUpdateBoardWithValidRequest() throws UserException, BadRequestException, IOException {

        UpdateBoardRequest updateBoardRequest = new UpdateBoardRequest();
        updateBoardRequest.setName("Test Board Update");
        updateBoardRequest.setBoardVisibility("PRIVATE");

        when(userPrincipalMock.getEmail()).thenReturn("admin@gmail.com");
        when(userRepositoryMock.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));

        when(boardRepositoryMock.save(any(Board.class))).thenReturn(board);
        when(boardRepositoryMock.findByBoardTag(any(String.class))).thenReturn(Optional.of(board));

        BoardResponse response = boardServiceImplMock.updateBoard(updateBoardRequest, userPrincipalMock);

        assertNotNull(response);
        assertEquals("Test Board Update", response.getName());
        assertEquals("PRIVATE", response.getBoardVisibility());
    }
}