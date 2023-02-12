package com.thullo.event;

import com.thullo.data.model.BoardIdWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class BoardRefGeneratorOnShutDown implements ApplicationListener<ContextClosedEvent> {

    private final ConcurrentMap<String, AtomicLong> boardTaskCounters;

    public BoardRefGeneratorOnShutDown(BoardIdWrapper boardIdWrapper) {
        this.boardTaskCounters = boardIdWrapper.getBoardTaskCounters();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        updateNextId();
    }

    private void updateNextId() {
        log.info("Updating next id values to file");
        // Update the next ID value in the file for each board
        boardTaskCounters.forEach((boardRef, taskCounter) -> {
            try {
                Path filePath = Paths.get("src/main/resources/static/next_board_id");
                List<String> lines = List.of(boardRef + "," + taskCounter.get());
                Files.write(filePath, lines, StandardCharsets.UTF_8);
                log.info("Successfully updated next id value for board ref: " + boardRef);
            } catch (IllegalStateException e) {
                // Log the exception and return without updating the next id
                log.warn("Update of next id failed, shutdown in progress: " + e.getMessage());
            } catch (Exception e) {
                // Log the exception
                log.warn("Exception while updating next id: " + e.getMessage());
            }
        });
    }

    public void registerShutdownHook() {
        log.info("Registering shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(this::updateNextId));
    }

    @PreDestroy
    public void init() {
        log.info("Initializing shutdown hook");
        registerShutdownHook();
    }

}

