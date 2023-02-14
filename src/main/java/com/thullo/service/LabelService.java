package com.thullo.service;

import com.thullo.data.model.Label;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.payload.request.LabelRequest;

import java.util.List;

public interface LabelService {
    Label createLabel(String boardRef, LabelRequest request) throws ResourceNotFoundException, ThulloException;
    void removeLabelFromTask(Long labelId, String boardRef) throws ResourceNotFoundException;

    List<Label> getBoardLabel(Long boardId) throws ResourceNotFoundException;

    Label updateLabelOnTask(String boardRef, Long labelId, LabelRequest request) throws ResourceNotFoundException;
}
