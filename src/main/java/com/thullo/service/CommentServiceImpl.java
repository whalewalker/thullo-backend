package com.thullo.service;

import com.thullo.data.model.Comment;
import com.thullo.data.model.NotificationType;
import com.thullo.data.model.Task;
import com.thullo.data.model.User;
import com.thullo.data.repository.CommentRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.CommentRequest;
import com.thullo.web.payload.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper mapper;
    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final NotificationService notificationService;

    @Override
    public CommentResponse createComment(String boardRef, String email, CommentRequest request) throws ResourceNotFoundException {
        Comment comment = mapper.map(request, Comment.class);
        User createdBy = userRepository.findUserByEmail(email);
        Task task = getTask(boardRef);
        List<User> mentionedUsers = userRepository.findAllByEmails(request.getMentionedUsers());
        comment.setMentionedUsers(mentionedUsers);
        comment.setTask(task);
        comment.setCreatedBy(createdBy);
        commentRepository.save(comment);

        String title = "You have been mentioned in a comment on task: " + task.getBoardRef();
        String message = "You have been mentioned in a comment on task " + task.getBoardRef() + ": " + request.getMessage();
        notificationService.sendNotificationsToUsers(mentionedUsers, message, title, NotificationType.MENTIONED_IN_COMMENT);

        CommentResponse response = mapper.map(comment, CommentResponse.class);
        response.setCreatedBy(createdBy.getName());
        response.setImageUrl(createdBy.getImageUrl());

        return response;
    }

    @Override
    public CommentResponse editComment(String boardRef, Long commentId, CommentRequest request) throws ResourceNotFoundException {
        Comment comment = getComment(commentId);
        mapper.map(request, comment);

        List<String> existingMentionedUserEmails = comment.getMentionedUsers()
                .stream().map(User::getEmail)
                .collect(Collectors.toList());

        Set<String> newMentionedUserEmails = request.getMentionedUsers();
        existingMentionedUserEmails.forEach(newMentionedUserEmails::remove);
        if (newMentionedUserEmails.isEmpty()) {
            comment = commentRepository.save(comment);
        } else {
            List<User> newMentionedUsers = userRepository.findAllByEmails(newMentionedUserEmails);
            comment.setMentionedUsers(newMentionedUsers);
            comment = commentRepository.save(comment);

            String title = "You have been mentioned in a comment on task: " + boardRef;
            String message = "You have been mentioned in a comment on task " + boardRef + ": " + request.getMessage();
            notificationService.sendNotificationsToUsers(newMentionedUsers, message, title, NotificationType.MENTIONED_IN_COMMENT);
        }

        CommentResponse response = mapper.map(comment, CommentResponse.class);
        response.setCreatedBy(comment.getCreatedBy().getName());
        response.setImageUrl(comment.getCreatedBy().getImageUrl());

        return response;
    }

    @Override
    public void deleteComment(String boardRef, Long commentId) throws ResourceNotFoundException {
        Task task = getTask(boardRef);
        Optional<Comment> commentToDelete = task.getComments()
                .stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst();

        commentToDelete.ifPresent(comment -> {
            task.getComments().remove(comment);
            commentRepository.delete(comment);
        });
    }


    private Comment getComment(Long commentId) throws ResourceNotFoundException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    private Task getTask(String boardRef) throws ResourceNotFoundException {
        return taskRepository.findByBoardRef(boardRef)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }
}
