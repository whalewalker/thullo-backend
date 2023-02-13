package com.thullo.service;

import com.thullo.data.model.Label;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.LabelRequest;

public interface LabelService {
    Label createLabel(LabelRequest request) throws ResourceNotFoundException;
}
