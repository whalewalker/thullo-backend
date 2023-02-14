package com.thullo.service;

import com.thullo.data.model.NotificationType;
import com.thullo.data.model.User;
import com.thullo.web.exception.ResourceNotFoundException;

import java.util.List;

public interface NotificationService {
    void markNotificationAsViewed(Long id) throws ResourceNotFoundException;
    void sendNotificationsToUsers(List<User> users, String message, String title, NotificationType notificationType);
    void sendNotificationToUser(User user, String message, String title, NotificationType notificationType);
}
