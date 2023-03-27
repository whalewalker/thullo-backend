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
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(MockitoJUnitRunner.class)
class BoardServiceImplTest {
    private ModelMapper mapperMock;
    private BoardRepository boardRepositoryMock;
    private UserRepository userRepositoryMock;
    private FileService fileServiceMock;

    private RoleServiceImpl roleServiceMock;

    private NotificationService notificationServiceMock;


    private BoardServiceImpl boardServiceImplMock;
    private Board board;
    private BoardRequest boardRequest;
    private UserPrincipal userPrincipal;
    String boardName = "DevDegree challenge";
    String imageUrl = "http://localhost:8080/api/v1/thullo/files/123e4567-e89b-12d3-a456-426655440000";

    @BeforeEach
    void setUp() {
        mapperMock = mock(ModelMapper.class);
        boardRepositoryMock = mock(BoardRepository.class);
        userRepositoryMock = mock(UserRepository.class);
        fileServiceMock = mock(FileService.class);
        roleServiceMock = mock(RoleServiceImpl.class);
        notificationServiceMock = mock(NotificationServiceImpl.class);
        boardServiceImplMock = new BoardServiceImpl(boardRepositoryMock, mapperMock, fileServiceMock, userRepositoryMock, roleServiceMock, notificationServiceMock);

        board = new Board();
        board.setName(boardName);

        boardRequest = new BoardRequest();
        boardRequest.setName(boardName);


//        board.setTaskColumns(
//                List.of(
//                        new TaskColumn("Backlog \uD83E\uDD14", new Board()),
//                        new TaskColumn("In Progress \uD83D\uDCDA", new Board()),
//                        new TaskColumn("In Review ⚙️", new Board()),
//                        new TaskColumn("Completed \uD83D\uDE4C\uD83C\uDFFD", new Board()))
//
//        );

        userPrincipal = new UserPrincipal(
                1L,
                "Ismail Abdullah"
                , "admin@gmail.com",
                "password"
                , true
                , List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        UpdateBoardRequest request = new UpdateBoardRequest();
        request.setName("Thullo Challenge");
        request.setBoardVisibility("public");
    }

    @Test
    void testCreateBoard_withBoardName_createANewBoard() {
        try {
            when(userRepositoryMock.findByEmail(anyString()))
                    .thenReturn(Optional.of(new User()));
            when(mapperMock.map(boardRequest, Board.class))
                    .thenReturn(board);
            when(mapperMock.map(board, Board.class))
                    .thenReturn(board);
            when(boardRepositoryMock.save(board)).thenReturn(board);

            BoardResponse actualResponse = boardServiceImplMock.createBoard(boardRequest, userPrincipal);

            verify(mapperMock).map(boardRequest, Board.class);
            verify(boardRepositoryMock).save(board);
            verify(userRepositoryMock).findByEmail(userPrincipal.getEmail());
            assertEquals(boardName, actualResponse.getName());
            assertEquals("PRIVATE", actualResponse.getBoardVisibility());
        } catch (UserException | IOException | BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateBoard_WithBoardNameAndCoverImage_createANewBoard() throws IOException, UserException, BadRequestException {
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/screenshot.png");
        boardRequest.setFile(multipartFile);
        boardRequest.setRequestUrl("http://localhost:8080/api/v1/thullo");

        board.setImageUrl(imageUrl);

        when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        when(mapperMock.map(boardRequest, Board.class))
                .thenReturn(board);

        when(fileServiceMock.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl()))
                .thenReturn(imageUrl);

        when(boardRepositoryMock.save(board)).thenReturn(board);

        when(mapperMock.map(board, Board.class))
                .thenReturn(board);


        BoardResponse actualResponse = boardServiceImplMock.createBoard(boardRequest, userPrincipal);

        verify(mapperMock).map(boardRequest, Board.class);
        verify(boardRepositoryMock).save(board);
        verify(userRepositoryMock).findByEmail(userPrincipal.getEmail());
        verify(fileServiceMock).uploadFile(multipartFile, boardRequest.getRequestUrl());
        assertEquals(boardName, actualResponse.getName());
        assertEquals(imageUrl, actualResponse.getImageUrl());
    }


    @Test
    void testCreateBoard_WithValidRequest_createANewBoardWith4TaskColumns() throws IOException, UserException, BadRequestException {
        MultipartFile multipartFile = getMultipartFile("src/main/resources/static/code.png");
        boardRequest.setFile(multipartFile);
        boardRequest.setRequestUrl("http://localhost:8080/api/v1/thullo");

        when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        when(mapperMock.map(boardRequest, Board.class))
                .thenReturn(board);

        when(fileServiceMock.uploadFile(boardRequest.getFile(), boardRequest.getRequestUrl()))
                .thenReturn(imageUrl);

        when(boardRepositoryMock.save(board)).thenReturn(board);

        when(mapperMock.map(board, Board.class))
                .thenReturn(board);

        BoardResponse actualResponse = boardServiceImplMock.createBoard(boardRequest, userPrincipal);

//        assertEquals(4, actualResponse.getTasks().size());
    }

    @Test
    void shouldReturnAllTaskColumnWhenValidBoardId() throws BadRequestException {
//        board.getTaskColumns().addAll(
//                List.of(
//                        createTaskColumn("Backlog"),
//                        createTaskColumn("In Progress"),
//                        createTaskColumn("In Review"),
//                        createTaskColumn("Completed"))
//        );

        when(boardRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.ofNullable(board));

//        Board userBoard = boardService.getBoard(1L);

        verify(boardRepositoryMock).findById(1L);
//        assertAll(() -> {
//            assertEquals(board.getName(), userBoard.getName());
//            assertEquals(4, board.getTaskColumns().size());
//            for(TaskColumn column : board.getTaskColumns()){
//                assertNotNull(column);
//                assertEquals(1, column.getTasks().size());
//                for (Task task : column.getTasks()){
//                    assertNotNull(task);
//                }
//            }
//        });
    }

    @Test
    void shouldReturnAllBoardWhenValidUser() throws UserException {
        User boardOwner = new User();
        when(boardRepositoryMock.getAllByUserOrderByCreatedAtAsc(any(User.class)))
                .thenReturn(new ArrayList<>(List.of(
                        new Board(), new Board(), new Board() )));
        when(userRepositoryMock.findByEmail(anyString()))
                .thenReturn(Optional.of(boardOwner));

        List<BoardResponse> boards = boardServiceImplMock.getBoards(userPrincipal);
        verify(boardRepositoryMock).getAllByUserOrderByCreatedAtAsc(boardOwner);
        verify(userRepositoryMock).findByEmail("ismail@gmail.com");

        assertNotNull(boards);
        assertEquals(3, boards.size());
        for (BoardResponse userBoard : boards) {
            assertNotNull(userBoard);
        }
    }

//    private TaskColumn createTaskColumn(String name) {
//        TaskColumn column = new TaskColumn();
//        column.setName(name);
////        column.getTasks().add(new Task());
//        return column;
//    }

    public MultipartFile getMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    }

    @Test
    void testThatBoardCanBeUpdated(){
        UpdateBoardRequest request = new UpdateBoardRequest();
    }
}