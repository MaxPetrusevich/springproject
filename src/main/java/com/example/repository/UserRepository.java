package com.example.repository;

import com.example.entity.User;
import com.example.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countByEnabledTrue();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = :enabled")
    long countByEnabled(@Param("enabled") boolean enabled);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);
    
    @Query(value = """
        SELECT DATE_TRUNC('month', created_at) as date, COUNT(*) as count 
        FROM users 
        GROUP BY DATE_TRUNC('month', created_at) 
        ORDER BY date DESC 
        LIMIT 12
        """, 
        nativeQuery = true)
    List<Object[]> countUsersByMonth();
    
    List<User> findTop5ByOrderByCreatedAtDesc();

    Page<User> findByEmailContainingOrFirstNameContainingOrLastNameContaining(
        String email, String firstName, String lastName, Pageable pageable);

    Page<User> findByEmailContainingOrFirstNameContainingOrLastNameContainingAndRole(
            String email, String firstName, String lastName, UserRole role, Pageable pageable);

    Page<User> findByEmailContainingOrFirstNameContainingOrLastNameContainingAndEnabled(
        String email, String firstName, String lastName, Boolean enabled, Pageable pageable);

    Page<User> findByEmailContainingOrFirstNameContainingOrLastNameContainingAndRoleAndEnabled(
        String email, String firstName, String lastName, UserRole role, Boolean enabled, Pageable pageable);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByEnabled(Boolean enabled, Pageable pageable);

    Page<User> findByRoleAndEnabled(UserRole role, Boolean enabled, Pageable pageable);

    List<User> findByRole(UserRole role);
} 