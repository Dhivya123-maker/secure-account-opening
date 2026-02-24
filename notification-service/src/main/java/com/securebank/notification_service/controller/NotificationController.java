package com.securebank.notification_service.controller;

import com.securebank.notification_service.dto.request.NotificationRequest;
import com.securebank.notification_service.dto.response.ApiResponse;
import com.securebank.notification_service.dto.response.NotificationResponse;
import com.securebank.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent successfully", response));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByCustomerId(
            @PathVariable Long customerId) {
        List<NotificationResponse> response =
                notificationService.getNotificationsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByStatus(
            @PathVariable String status) {
        List<NotificationResponse> response =
                notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", response));
    }
}