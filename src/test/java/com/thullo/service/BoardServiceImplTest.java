package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ThulloException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceImplTest {
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileService fileService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private BoardServiceImpl boardService;

    private User user;
    private Board board;
    private BoardRequest boardRequest;
    private BoardResponse boardResponse;

    @BeforeEach
    void setUp() {
        boardRequest = new BoardRequest();
        boardRequest.setName("Test Board");
        boardRequest.setBoardVisibility("PUBLIC");
        boardRequest.setRequestUrl("http://localhost:8080");
        boardRequest.setFile(new MockMultipartFile("image.jpg", new byte[0]));

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        board = new Board();
        board.setName("Test Board");

        boardResponse = new BoardResponse();
        boardResponse.setName(boardRequest.getName());
        boardResponse.setBoardVisibility(boardRequest.getBoardVisibility());
    }

    @Test
    void testCreateBoard() throws Exception {
        // mock repository and service calls
        when(userRepository.findByEmail(userPrincipal.getEmail())).thenReturn(Optional.ofNullable(user));
        when(boardRepository.existsByNameAndCreatedBy(boardRequest.getName(), user)).thenReturn(false);
        when(modelMapper.map(boardRequest, Board.class)).thenReturn(new Board());
        when(modelMapper.map(any(Board.class), eq(BoardResponse.class))).thenReturn(boardResponse);
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        // call service method
        BoardResponse boardResponse = boardService.createBoard(boardRequest, userPrincipal);

        // verify repository and service calls
        verify(userRepository).findByEmail(userPrincipal.getEmail());
        verify(boardRepository).existsByNameAndCreatedBy(boardRequest.getName(), user);
        verify(boardRepository).save(any(Board.class));
        verify(fileService).uploadFile(any(MultipartFile.class), eq(boardRequest.getRequestUrl()));
        verify(modelMapper).map(any(Board.class), eq(BoardResponse.class));

        // verify response
        assertNotNull(boardResponse);
        assertEquals(boardRequest.getName(), boardResponse.getName());
        assertEquals(boardRequest.getBoardVisibility(), boardResponse.getBoardVisibility());
    }


    @Test
    void testCreateBoardWithEmptyName() {
        boardRequest.setName("");
        assertThrows(BadRequestException.class, () -> boardService.createBoard(boardRequest, userPrincipal));
    }

    @Test
    void testCreateBoardWithNameAlreadyExists() {
        when(userRepository.findByEmail(userPrincipal.getEmail())).thenReturn(Optional.ofNullable(user));
        when(boardRepository.existsByNameAndCreatedBy(boardRequest.getName(), user)).thenReturn(true);
        assertThrows(ThulloException.class, () -> boardService.createBoard(boardRequest, userPrincipal));
    }


    @Test
    void testGetBoardWithExistingBoardTag() throws Exception {
        String boardTag = "ABC";
        Board board = new Board();
        board.setId(1L);
        board.setName("Test Board");
        board.setBoardTag(boardTag);

        BoardResponse expectedResponse = new BoardResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName("Test Board");
        expectedResponse.setBoardTag(boardTag);


        when(boardRepository.findByBoardTag(boardTag)).thenReturn(Optional.of(board));
        when(modelMapper.map(any(Board.class), eq(BoardResponse.class))).thenReturn(expectedResponse);


        BoardResponse actualResponse = boardService.getBoard(boardTag);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetBoardWithNonExistingBoardTag() {
        String boardTag = "XYZ";

        when(boardRepository.findByBoardTag(boardTag)).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class,
                () -> boardService.getBoard(boardTag));
    }

    @Test
    void testGetBoards() {
        // Mock repository
        List<Board> mockBoards = new ArrayList<>();
        Board board1 = new Board();
        board1.setId(1L);
        board1.setName("Board 1");
        board1.setCreatedBy(new User("user1@example.com"));
        board1.setCollaborators(new HashSet<>(Collections.singletonList(new User("user2@example.com"))));
        mockBoards.add(board1);
        Board board2 = new Board();
        board2.setId(2L);
        board2.setName("Board 2");
        board2.setCreatedBy(new User("user3@example.com"));
        board2.setCollaborators(new HashSet<>(Collections.singletonList(new User("user1@example.com"))));
        mockBoards.add(board2);
        when(boardRepository.findAllByCreatedBy("user1@example.com")).thenReturn(mockBoards.subList(0, 1));
        when(boardRepository.findAllByCollaborators("user1@example.com")).thenReturn(mockBoards.subList(1, 2));
        when(boardRepository.findAllByContributors("user1@example.com")).thenReturn(Collections.emptyList());

        // Test with no filter params
        Map<String, String> filterParams = new HashMap<>();
        List<BoardResponse> boardResponses = boardService.getBoards(userPrincipal, filterParams);
        assertEquals(2, boardResponses.size());
        assertEquals(1L, boardResponses.get(0).getId());
        assertEquals("Board 1", boardResponses.get(0).getName());
        assertEquals(2L, boardResponses.get(1).getId());
        assertEquals("Board 2", boardResponses.get(1).getName());

        // Test with "contributor" filter param
        filterParams.put("contributor", "true");
        boardResponses = boardService.getBoards(userPrincipal, filterParams);
        assertEquals(1, boardResponses.size());
        assertEquals(2L, boardResponses.get(0).getId());
        assertEquals("Board 2", boardResponses.get(0).getName());

        // Test with "collaborator" filter param
        filterParams.remove("contributor");
        filterParams.put("collaborator", "true");
        boardResponses = boardService.getBoards(userPrincipal, filterParams);
        assertEquals(1, boardResponses.size());
        assertEquals(1L, boardResponses.get(0).getId());
        assertEquals("Board 1", boardResponses.get(0).getName());
    }

}