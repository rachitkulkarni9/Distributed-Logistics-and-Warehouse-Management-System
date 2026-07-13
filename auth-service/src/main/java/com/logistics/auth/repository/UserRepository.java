package com.logistics.auth.repository;

import com.logistics.auth.entity.User;
import com.logistics.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.accountNonLocked = true AND u.email = :email")
    Optional<User> findActiveByEmail(String email);
}
