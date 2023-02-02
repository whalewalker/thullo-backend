package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.repository.BoardRepository;
import com.thullo.web.payload.request.BoardRequest;
import com.thullo.web.payload.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper mapper;

    private final FileService fileService;
    /**
     * Creates a new board based on the provided board request.
     *
     * @param boardRequest The request containing the information for the new board to be created.
     * @return A response object containing the result of the board creation process.
     */
    @Override
    public BoardResponse createBoard(BoardRequest boardRequest) {
        Board board = mapper.map(boardRequest, Board.class);
        String imageUrl = fileService.uploadFile(boardRequest.getFile());
        board.setImageUrl(imageUrl);
        Board saveBoard = boardRepository.save(board);
        return mapper.map(saveBoard, BoardResponse.class);
    }
}
