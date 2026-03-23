package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.Notification;
import com.bagygo.bagygo_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
}
