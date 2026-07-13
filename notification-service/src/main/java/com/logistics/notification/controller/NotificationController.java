package com.logistics.notification.controller;

import com.logistics.common.response.ApiResponse;
import com.logistics.common.response.PagedResponse;
import com.logistics.common.util.PageUtils;
import com.logistics.notification.entity.NotificationLog;
import com.logistics.notification.repository.NotificationLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification history")
public class NotificationController {

    private final NotificationLogRepository logRepository;

    @GetMapping
    @Operation(summary = "Get notification history for a recipient")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationLog>>> listByRecipient(
            @RequestParam String recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var paged = logRepository.findByRecipientId(recipientId,
            PageRequest.of(page, size, Sort.by("sentAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(PageUtils.toPagedResponse(paged)));
    }
}
