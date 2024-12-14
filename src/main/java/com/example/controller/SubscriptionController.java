package com.example.controller;

import com.example.dto.SubscriptionDto;
import com.example.entity.User;
import com.example.mapper.EntityMapper;
import com.example.service.SubscriptionService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController extends BaseController {
    
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final EntityMapper mapper;
    
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubscriptionDto>> getMySubscriptions() {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(mapper.toSubscriptionDtos(
            subscriptionService.getUserSubscriptions(userId)
        ));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> getSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toSubscriptionDto(
            subscriptionService.findById(id)
        ));
    }
    
    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> subscribe(@RequestParam Long planId) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(mapper.toSubscriptionDto(
            subscriptionService.subscribe(userId, planId)
        ));
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> cancelSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toSubscriptionDto(
            subscriptionService.cancelSubscription(id)
        ));
    }
    
    @PostMapping("/{id}/renew")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> renewSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toSubscriptionDto(
            subscriptionService.renewSubscription(id)
        ));
    }
} 