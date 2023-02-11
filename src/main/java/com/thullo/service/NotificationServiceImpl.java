package com.thullo.service;

import com.thullo.data.model.Notification;
import com.thullo.data.repository.NotificationRepository;
import com.thullo.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;
    @Override
    public void markNotificationAsViewed(Long id) throws ResourceNotFoundException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(format("Notification with id %s not found", id)));
        notification.setViewed(true);
        notificationRepository.save(notification);
    }
}
