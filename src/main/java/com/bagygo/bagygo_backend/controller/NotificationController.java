package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.response.NotificationResponse;
import com.bagygo.bagygo_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread")
    public List<NotificationResponse> getUnreadNotifications(Authentication authentication) {
        return notificationService.getUnreadNotificationsForUser(authentication.getName());
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications(Authentication authentication) {
        return notificationService.getAllNotificationsForUser(authentication.getName());
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, Authentication authentication) {
        notificationService.markAsRead(id, authentication.getName());
    }

    @PutMapping("/read-all")
    public void markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
    }
}
