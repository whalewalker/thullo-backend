package com.thullo.service;

import com.thullo.data.model.Notification;
import com.thullo.data.model.NotificationType;
import com.thullo.data.model.User;
import com.thullo.data.repository.NotificationRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void markNotificationAsViewed(Long id) throws ResourceNotFoundException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(format("Notification with id %s not found", id)));
        notification.setViewed(true);
        notificationRepository.save(notification);
    }

    public void sendNotificationsToUsers(List<User> users, String message, String title, NotificationType notificationType) {
        for (User user : users) {
            sendNotificationToAUser(user, message, title, notificationType);
        }
    }

    @Override
    public void sendNotificationToUser(User user, String message, String title, NotificationType notificationType) {
       sendNotificationToAUser(user, message, title, notificationType);
    }


    private void sendNotificationToAUser(User user, String message, String title, NotificationType notificationType) {
        Notification notification = new Notification(
                user,
                title,
                message,
                notificationType
        );
        user.addNotification(notification);
        notificationRepository.save(notification);
    }
}
