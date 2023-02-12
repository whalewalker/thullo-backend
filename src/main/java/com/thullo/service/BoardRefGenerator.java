package com.thullo.service;

import com.thullo.data.model.Board;
import com.thullo.data.model.BoardId;
import com.thullo.data.model.TaskColumn;
import com.thullo.data.repository.BoardIdRepository;
import org.springframework.stereotype.Service;

@Service
public class BoardRefGenerator {
    private final BoardIdRepository boardIdRepository;

    public BoardRefGenerator(BoardIdRepository boardIdRepository) {
        this.boardIdRepository = boardIdRepository;
    }

    public String generateBoardRef(TaskColumn taskColumn) {
        Board board = taskColumn.getBoard();
        String boardTag = board.getBoardTag();

        BoardId boardId = boardIdRepository.findByBoardTag(boardTag)
                .orElseGet(() -> new BoardId(boardTag, 1L));

        String boardRef = boardTag + "-" + boardId.getNextId();
        boardId.setNextId(boardId.getNextId() + 1);

        boardIdRepository.save(boardId);

        return boardRef;
    }
}

