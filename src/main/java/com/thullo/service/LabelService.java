package com.thullo.service;

import com.thullo.data.model.Label;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.LabelRequest;

import java.util.List;

public interface LabelService {
    Label createLabel(LabelRequest request) throws ResourceNotFoundException;
    void removeLabelFromTask(Long labelId, String boardRef) throws ResourceNotFoundException;

    List<Label> getBoardLabel(Long boardId) throws ResourceNotFoundException;
}
