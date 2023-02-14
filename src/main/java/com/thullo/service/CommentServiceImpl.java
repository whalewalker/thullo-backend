package com.thullo.service;

import com.thullo.data.model.Comment;
import com.thullo.data.model.Task;
import com.thullo.data.model.User;
import com.thullo.data.repository.CommentRepository;
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

import static com.thullo.data.model.NotificationType.MENTIONED_IN_COMMENT;

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

        String title = "You have been mentioned in a comment on task: " + task.getBoardRef();
        String message = "You have been mentioned in a comment on task " + task.getBoardRef() + ": " + request.getMessage();
        notificationService.sendNotificationsToUsers(mentionedUsers, message, title, MENTIONED_IN_COMMENT);
        return savedComment;
    }


}
