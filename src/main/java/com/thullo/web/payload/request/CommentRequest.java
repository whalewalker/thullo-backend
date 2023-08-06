package com.thullo.web.payload.request;

import lombok.Data;

import java.util.Set;

@Data
public class CommentRequest {
    private Long id;
    private String message;
    private Set<String> mentionedUsers;
}
