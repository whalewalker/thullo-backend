package com.thullo.web.payload.response;

import com.thullo.data.model.Attachment;
import com.thullo.data.model.Comment;
import com.thullo.data.model.Label;
import com.thullo.data.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class TaskResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private Long position;
    private String boardRef;
    private String description;
    private List<Comment> comments = new ArrayList<>();
    private Set<Label> labels = new LinkedHashSet<>();
    private List<Attachment> attachments = new ArrayList<>();
    private Set<User> contributors = new LinkedHashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
