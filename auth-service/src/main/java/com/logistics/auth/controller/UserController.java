package com.logistics.auth.controller;

import com.logistics.auth.entity.User;
import com.logistics.auth.service.UserService;
import com.logistics.common.dto.auth.AuthResponse;
import com.logistics.common.response.ApiResponse;
import com.logistics.common.response.PagedResponse;
import com.logistics.common.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * User profile management. Requires authentication.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and administration")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<AuthResponse.UserSummary>> getMe(
            @RequestHeader("X-User-Id") String userId) {
        User user = userService.findById(UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(userService.toSummary(user)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (admin only)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse.UserSummary>> getById(@PathVariable UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(userService.toSummary(user)));
    }

    @GetMapping
    @Operation(summary = "List all users (admin only)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<AuthResponse.UserSummary>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuthResponse.UserSummary> result = userService.findAll(pageable)
            .map(userService::toSummary);
        return ResponseEntity.ok(ApiResponse.success(PageUtils.toPagedResponse(result)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a user (admin only)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
    }
}
