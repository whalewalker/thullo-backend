package com.thullo.service;

import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.TaskColumnRequest;
import com.thullo.web.payload.response.TaskColumnResponse;

import java.util.Map;

public interface TaskColumnService {
    TaskColumnResponse createTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag, UserPrincipal principal) throws UserException, ThulloException, BadRequestException;

    TaskColumnResponse editTaskColumn(TaskColumnRequest taskColumnRequest, String boardTag) throws ResourceNotFoundException, ThulloException, BadRequestException;

    TaskColumnResponse getTaskColumn(Map<String, String> params, String boardTag) throws ResourceNotFoundException, BadRequestException;

    void deleteTaskColumn(TaskColumnRequest taskColumnRequest, String board, UserPrincipal userPrincipal) throws ResourceNotFoundException, ThulloException, UserException, BadRequestException;
}
