package com.securebank.notification_service.repository;

import com.securebank.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerId(Long customerId);

    List<Notification> findByStatus(String status);

    List<Notification> findByCustomerIdAndNotificationType(Long customerId,
                                                           String notificationType);
}