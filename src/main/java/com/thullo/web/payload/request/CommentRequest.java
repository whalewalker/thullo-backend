package com.thullo.web.payload.request;

import com.thullo.data.model.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentRequest {
    private String message;
    private Long taskId;
    private List<User> mentionedUsers;
}
