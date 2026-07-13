package com.logistics.auth.service;

import com.logistics.auth.entity.User;
import com.logistics.auth.repository.UserRepository;
import com.logistics.common.dto.auth.AuthResponse;
import com.logistics.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * CRUD operations on user profiles. Admin-facing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User updateUser(UUID id, String firstName, String lastName, String phone) {
        User user = findById(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID id) {
        User user = findById(id);
        user.setEnabled(false);
        userRepository.save(user);
        log.info("User deactivated: id={}", id);
    }

    public AuthResponse.UserSummary toSummary(User user) {
        return AuthResponse.UserSummary.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .build();
    }
}
