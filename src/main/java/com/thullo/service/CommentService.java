package com.thullo.service;

import com.thullo.data.model.Comment;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.CommentRequest;

public interface CommentService {
    Comment createComment(String boardRef, CommentRequest request) throws ResourceNotFoundException;
    Comment editComment(String boardRef, Long commentId,  CommentRequest request) throws ResourceNotFoundException;
}
