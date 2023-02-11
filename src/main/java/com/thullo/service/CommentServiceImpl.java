package com.thullo.service;

import com.thullo.data.model.*;
import com.thullo.data.repository.CommentRepository;
import com.thullo.data.repository.NotificationRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.CommentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper mapper;

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final NotificationRepository notificationRepository;

    @Override
    public Comment createComment(CommentRequest request) throws ResourceNotFoundException {
        Comment comment = mapper.map(request, Comment.class);

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found !"));

        List<User> mentionedUsers = userRepository.findAllById(
                request.getMentionedUsers().stream().map(User::getId).collect(Collectors.toList())
        );
        comment.setMentionedUsers(mentionedUsers);
        comment.setTask(task);

        Comment savedComment = commentRepository.save(comment);
        sendNotificationsToMentionedUsers(mentionedUsers, request.getMessage(), task.getBoardId());
        return savedComment;
    }

    private void sendNotificationsToMentionedUsers(List<User> mentionedUsers, String message, String taskName) {
        for (User user : mentionedUsers) {
            Notification notification = new Notification(
                    user,
                    "You have been mentioned in a comment on task: " + taskName,
                    "You have been mentioned in a comment on task " + taskName + ": " + message,
                    NotificationType.MENTIONED_IN_COMMENT
            );
            user.addNotification(notification);
            notificationRepository.save(notification);
         }
    }
}
