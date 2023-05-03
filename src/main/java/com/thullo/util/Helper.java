package com.thullo.util;

import com.thullo.data.model.*;
import com.thullo.web.payload.response.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Helper {

    public static boolean isValidToken(LocalDateTime expiryDate) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiryDate);
        return minutes >= 0;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String calculateFileSize(long size) {
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static String extractFileIdFromUrl(String imageUrl) {
        String fileUrl = imageUrl.substring(imageUrl.indexOf("files/") + 6);
        return fileUrl.split("\\.")[0];
    }

    public static boolean isOnServer(String envName) {
        return envName.trim().equals("server");
    }

    public static TaskResponse getTaskResponse(Task task, ModelMapper mapper) {
        TaskResponse taskResponse = mapper.map(task, TaskResponse.class);
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : task.getComments()) {
            CommentResponse commentResponse = mapper.map(comment, CommentResponse.class);
            commentResponses.add(commentResponse);
        }
        taskResponse.setComments(commentResponses);
        return mapper.map(task, TaskResponse.class);
    }

    public static TaskColumnResponse getTaskColumnResponse(TaskColumn taskColumn, ModelMapper mapper) {
        TaskColumnResponse taskColumnResponse = mapper.map(taskColumn, TaskColumnResponse.class);
        List<TaskResponse> taskResponses = getTaskResponses(taskColumn.getTasks(), mapper);
        taskColumnResponse.setTasks(taskResponses);
        return taskColumnResponse;
    }

    public static Set<UserResponse> getUserResponses(Set<User> users, ModelMapper mapper) {
        return users.stream().map(user -> getUserResponse(user, mapper)).collect(Collectors.toSet());
    }

    public static UserResponse getUserResponse(User user, ModelMapper mapper) {
        return mapper.map(user, UserResponse.class);
    }

    public static List<TaskResponse> getTaskResponses(List<Task> tasks, ModelMapper mapper) {
        return tasks.stream().map(task -> getTaskResponse(task, mapper))
                .collect(Collectors.toList());
    }

    public static BoardResponse getBoardResponseDetails(Board board, ModelMapper mapper) {
        BoardResponse boardResponse = mapper.map(board, BoardResponse.class);

        List<TaskColumn> taskColumns = board.getTaskColumns();
        List<TaskColumn> sortedColumns = taskColumns.stream()
                .sorted(Comparator.comparingInt(column ->
                        column.getName().equalsIgnoreCase("no status") ? 0 : 1))
                .collect(Collectors.toList());

        List<TaskColumnResponse> taskColumnResponses = sortedColumns.stream()
                .map(column -> getTaskColumnResponse(column, mapper)).collect(Collectors.toList());
        UserResponse userResponse = getUserResponse(board.getCreatedBy(), mapper);
        boardResponse.setCreatedBy(userResponse);
        boardResponse.setTaskColumn(taskColumnResponses);
        boardResponse.setCollaborators(getUserResponses(board.getCollaborators(), mapper));
        return boardResponse;
    }

    public static CommentResponse getCommentResponseDetails(Comment comment, ModelMapper mapper) {
        CommentResponse commentResponse = mapper.map(comment, CommentResponse.class);
        UserResponse userResponse = getUserResponse(comment.getCreatedBy(), mapper);
        commentResponse.setCreatedBy(userResponse);
        return commentResponse;
    }
}