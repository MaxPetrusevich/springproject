package com.example.controller.view;

import com.example.service.StatisticsService;
import com.example.service.UserService;
import com.example.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final StatisticsService statisticsService;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping(value = {"/dashboard", "/", ""})
    public String dashboard(Model model) {
        model.addAttribute("statistics", statisticsService.getAdminStatistics());
        model.addAttribute("recentUsers", userService.findTop5ByOrderByCreatedAtDesc());
        model.addAttribute("recentSubscriptions", subscriptionService.findTop5ByOrderByCreatedAtDesc());
        return "admin/dashboard";
    }
} 