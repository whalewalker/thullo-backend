package com.thullo.data.model;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class BoardIdWrapper {
    private final ConcurrentHashMap<String, AtomicLong> boardTaskCounters;

    public ConcurrentMap<String, AtomicLong> getBoardTaskCounters() {
        return boardTaskCounters;
    }

    public String generateTaskId(String boardId) {
        AtomicLong taskCounter = boardTaskCounters.computeIfAbsent(boardId, k -> {
            long nextId = getNextIdFromFile(boardId);
            saveNextIdToFile(boardId, nextId + 1);
            return new AtomicLong(nextId);
        });
        long currentId = taskCounter.getAndIncrement();
        return boardId + "-" + currentId;
    }

    private long getNextIdFromFile(String boardId) {
        File file = new File("src/main/resources/static/next_board_id");
        if (!file.exists() || file.length() == 0) {
            return 1;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            String[] values = line.split(",");
            String id = values[0];
            long nextId = Long.parseLong(values[1]);
            if (id.equals(boardId)) {
                return nextId;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private void saveNextIdToFile(String boardId, long nextId) {
        File file = new File("src/main/resources/static/next_board_id");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(boardId + "," + nextId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

