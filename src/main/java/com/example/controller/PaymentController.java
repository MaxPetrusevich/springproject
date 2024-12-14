package com.example.controller;

import com.example.dto.PaymentDto;
import com.example.entity.User;
import com.example.mapper.EntityMapper;
import com.example.service.PaymentService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController extends BaseController {
    
    private final PaymentService paymentService;
    private final UserService userService;
    private final EntityMapper mapper;
    
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentDto>> getMyPayments() {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(mapper.toPaymentDtos(
            paymentService.getAllUserPayments(userId)
        ));
    }
    
    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentDto>> getRecentPayments() {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        return ResponseEntity.ok(mapper.toPaymentDtos(
            paymentService.getRecentPayments(userId)
        ));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toPaymentDto(
            paymentService.getPaymentById(id)
        ));
    }
} 