package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.response.NotificationResponse;
import com.bagygo.bagygo_backend.entity.Notification;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.NotificationType;
import com.bagygo.bagygo_backend.repository.NotificationRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(User recipient, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUnreadNotificationsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public List<NotificationResponse> getAllNotificationsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public void markAsRead(Long notificationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Access Denied");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> unread = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
