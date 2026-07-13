package com.logistics.notification.repository;

import com.logistics.notification.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    Page<NotificationLog> findByRecipientId(String recipientId, Pageable pageable);
}
