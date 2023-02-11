package com.thullo.service;

import com.thullo.web.exception.ResourceNotFoundException;

public interface NotificationService {
    void markNotificationAsViewed(Long id) throws ResourceNotFoundException;
}
