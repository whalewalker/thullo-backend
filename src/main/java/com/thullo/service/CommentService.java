package com.thullo.service;

import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.CommentRequest;
import com.thullo.web.payload.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(String boardRef, String createdBy, CommentRequest request) throws ResourceNotFoundException;

    CommentResponse editComment(String boardRef, Long commentId, CommentRequest request) throws ResourceNotFoundException;

    void deleteComment(String boardRef, Long commentId) throws ResourceNotFoundException;

    List<CommentResponse> getTaskComment(String boardRef) throws ResourceNotFoundException;
}
