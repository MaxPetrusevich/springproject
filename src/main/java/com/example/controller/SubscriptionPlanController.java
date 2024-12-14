package com.example.controller;

import com.example.dto.SubscriptionPlanDto;
import com.example.mapper.EntityMapper;
import com.example.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {
    
    private final SubscriptionPlanService subscriptionPlanService;
    private final EntityMapper mapper;
    
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDto>> getAllPlans() {
        return ResponseEntity.ok(mapper.toPlanDtos(
            subscriptionPlanService.findAll()
        ));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDto> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(
            subscriptionPlanService.getPlan(id)
        ));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<SubscriptionPlanDto> createPlan(@RequestBody SubscriptionPlanDto planDto) {
        return ResponseEntity.ok(mapper.toDto(
            subscriptionPlanService.createPlan(planDto)
        ));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanDto planDto
    ) {
        return ResponseEntity.ok(mapper.toDto(
            subscriptionPlanService.updatePlan(id, planDto)
        ));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
        return ResponseEntity.ok().build();
    }
} 