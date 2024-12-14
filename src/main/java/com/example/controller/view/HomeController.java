package com.example.controller.view;

import com.example.dto.ProductDto;
import com.example.mapper.EntityMapper;
import com.example.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final ProductService productService;
    private final EntityMapper entityMapper;
    @GetMapping("/")
    public String home(Model model) {
        List<ProductDto> productDtos = productService.getFeaturedProducts().stream().map(entityMapper::toDto).toList();
        model.addAttribute("featuredProducts", productDtos);
        return "home/index";
    }
} 