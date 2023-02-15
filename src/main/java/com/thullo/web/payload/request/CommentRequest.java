package com.thullo.web.payload.request;

import lombok.Data;

import java.util.Set;

@Data
public class CommentRequest {
    private String message;
    private Set<String> mentionedUsers;
}
