package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.Task;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.model.User;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.BoardRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    private ModelMapper mapper;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board board;
    private BoardRequest boardRequest;


    private UserPrincipal userPrincipal;
    String boardName = "DevDegree challenge";
    String imageUrl = "http://localhost:8080/api/v1/thullo/files/123e4567-e89b-12d3-a456-426655440000";

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setName(boardName);

        boardRequest = new BoardRequest();
        boardRequest.setName(boardName);

        board = new Board();
        board.setName(boardName);
        board.setTaskColumns(
                List.of(
                        new TaskColumn("Backlog \uD83E\uDD14", new Board()),
                        new TaskColumn("In Progress \uD83D\uDCDA", new Board()),
                        new TaskColumn("In Review ⚙️", new Board()),
                        new TaskColumn("Completed \uD83D\uDE4C\uD83C\uDFFD", new Board()))

        );

        userPrincipal = new UserPrincipal(
                1L,
                "Ismail Abdullah"
                , "admin@gmail.com",
                "password"
                , true
                , List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void testCreateBoard_withBoardName_createANewBoard() throws UserException, BadRequestException, IOException {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(mapper.map(boardRequest, Board.class))
                .thenReturn(board);
        when(mapper.map(board, Board.class))
                .thenReturn(board);
        when(boardRepository.save(board)).thenReturn(board);

        Board actualResponse = boardService.createBoard(boardRequest, userPrincipal);

        verify(mapper).map(boardRequest, Board.class);
        verify(boardRepository).save(board);
        verify(userRepository).findByEmail(userPrincipal.getEmail());
        assertEquals(boardName, actualResponse.getName());
    }

    @Test
    void testCreateBoard_WithBoardNameAndCoverImage_createANewBoard() throws IOException, UserException, BadRequestException {
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/code.png");
        boardRequest.setFile(multipartFile);
        boardRequest.setRequestUrl("http://localhost:8080/api/v1/thullo");

        board.setImageUrl(imageUrl);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        when(mapper.map(boardRequest, Board.class))
                .thenReturn(board);

        when(fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl()))
                .thenReturn(imageUrl);

        when(boardRepository.save(board)).thenReturn(board);

        when(mapper.map(board, Board.class))
                .thenReturn(board);


        Board actualResponse = boardService.createBoard(boardRequest, userPrincipal);

        verify(mapper).map(boardRequest, Board.class);
        verify(boardRepository).save(board);
        verify(userRepository).findByEmail(userPrincipal.getEmail());
        verify(fileService).uploadFile(multipartFile, boardRequest.getRequestUrl());
        assertEquals(boardName, actualResponse.getName());
        assertEquals(imageUrl, actualResponse.getImageUrl());
    }


    @Test
    void testCreateBoard_WithValidRequest_createANewBoardWith4TaskColumns() throws IOException, UserException, BadRequestException {
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/code.png");
        boardRequest.setFile(multipartFile);
        boardRequest.setRequestUrl("http://localhost:8080/api/v1/thullo");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        when(mapper.map(boardRequest, Board.class))
                .thenReturn(board);

        when(fileService.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl()))
                .thenReturn(imageUrl);

        when(boardRepository.save(board)).thenReturn(board);

        when(mapper.map(board, Board.class))
                .thenReturn(board);

        Board actualResponse = boardService.createBoard(boardRequest, userPrincipal);


        assertEquals(4, actualResponse.getTaskColumns().size());
    }

    @Test
    void shouldReturnAllTaskColumnWhenValidBoardId() throws BadRequestException {
        board.getTaskColumns().addAll(
                List.of(
                        createTaskColumn("Backlog"),
                        createTaskColumn("In Progress"),
                        createTaskColumn("In Review"),
                        createTaskColumn("Completed"))
        );

        when(boardRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(board));

        Board userBoard = boardService.getBoard(1L);

        verify(boardRepository).findById(1L);
        assertAll(() -> {
            assertEquals(board.getName(), userBoard.getName());
            assertEquals(4, board.getTaskColumns().size());
            for(TaskColumn column : board.getTaskColumns()){
                assertNotNull(column);
                assertEquals(1, column.getTasks().size());
                for (Task task : column.getTasks()){
                    assertNotNull(task);
                }
            }
        });
    }

    @Test
    void shouldReturnAllBoardWhenValidUser() throws UserException {
        User boardOwner = new User();
        when(boardRepository.getAllByUserOrderByCreatedAtDesc(any(User.class)))
                .thenReturn(new ArrayList<>(List.of(
                        new Board(), new Board(), new Board() )));
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(boardOwner));

        List<Board> boards = boardService.getBoards(userPrincipal);
        verify(boardRepository).getAllByUserOrderByCreatedAtDesc(boardOwner);
        verify(userRepository).findByEmail("ismail@gmail.com");

        assertNotNull(boards);
        assertEquals(3, boards.size());
        for(Board userBoard : boards){
            assertNotNull(userBoard);
        }
    }

    private TaskColumn createTaskColumn(String name) {
        TaskColumn column = new TaskColumn();
        column.setName(name);
        column.getTasks().add(new Task());
        return column;
    }

    public MultipartFile getMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    }
}