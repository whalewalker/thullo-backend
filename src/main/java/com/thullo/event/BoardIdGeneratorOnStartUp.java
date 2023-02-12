package com.thullo.event;

import com.thullo.data.model.BoardIdWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class BoardIdGeneratorOnStartUp implements ApplicationListener<ContextRefreshedEvent> {

    private final ConcurrentMap<String, AtomicLong> boardTaskCounters;

    public BoardIdGeneratorOnStartUp(BoardIdWrapper boardIdWrapper) {
        this.boardTaskCounters = boardIdWrapper.getBoardTaskCounters();
    }

    public void init() {
        // Load the latest ID value from a file for each board
        Path filePath = Paths.get("src/main/resources/static/next_board_id");
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String[] parts = line.split(",");
                String boardRef = parts[0];
                long nextId = Long.parseLong(parts[1]);
                boardTaskCounters.put(boardRef, new AtomicLong(nextId));
            }
        } catch (IOException e) {
            // Log the exception and return without updating the next id
            log.warn("Error reading from file: " + e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

}
