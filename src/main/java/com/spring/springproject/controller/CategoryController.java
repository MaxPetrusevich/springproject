package com.spring.springproject.controller;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.CategoryService;
import com.spring.springproject.service.interfaces.TypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final TypeService typeService;

    @Autowired
    public CategoryController(CategoryService categoryService, TypeService typeService) {
        this.categoryService = categoryService;
        this.typeService = typeService;
    }

    @GetMapping("/category-list")
    public String findAll(Model model) {
        model.addAttribute("list", categoryService.findAll());
        return "category/catList";
    }

    @GetMapping("/category-edit")
    public String editRedirect(Model model, HttpServletRequest request) {
        String catId = request.getParameter("catId");
        CategoryDto categoryDto = null;
        if (!StringUtils.isEmptyOrWhitespace(catId)) {
            Integer id = Integer.parseInt(catId);
            categoryDto = categoryService.findById(id);
        }
        model.addAttribute("unit", categoryDto);
        Set<TypeDto> set = typeService.findAll();
        model.addAttribute("types", typeService.findAll());
        return "category/catEdit";
    }

    @PostMapping("/category-edit")
    public String edit(HttpServletRequest request, Model model) {
        String catId = request.getParameter("catId");
        CategoryDto categoryDto = null;
        if (!StringUtils.isEmptyOrWhitespace(catId)) {
            Integer id = Integer.parseInt(catId);
            categoryDto = categoryService.findById(id);
        }
        if (categoryDto.getTypes() != null) {
            categoryDto.getTypes().removeAll(categoryDto.getTypes());
        }
        setEditParams(request, categoryDto);
        categoryService.update(categoryDto);
        return findAll(model);
    }

    @PostMapping("/category-delete")
    public String delete(HttpServletRequest request, Model model){
        String catId = request.getParameter("catId");

        if (!StringUtils.isEmptyOrWhitespace(catId)) {
            Integer id = Integer.parseInt(catId);
            categoryService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping("/category-add")
    public String add(Model model){
        model.addAttribute("types", typeService.findAll());
        return "/category/catAdd";
    }
    @PostMapping("/category-add")
    public String add(HttpServletRequest request, Model model){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setTypes(new HashSet<>());
        categoryDto = categoryService.save(categoryDto);
        setEditParams(request, categoryDto);
        categoryService.update(categoryDto);
        return findAll(model);
    }

    private void setEditParams(HttpServletRequest request, CategoryDto categoryDto) {
        categoryDto.setName(request.getParameter("name"));
        String[] typeIdes = request.getParameterValues("typeId");
        if (typeIdes != null) {
            for (String typeId :
                    typeIdes) {
                if (!StringUtils.isEmptyOrWhitespace(typeId)) {
                    Integer id = Integer.parseInt(typeId);
                    TypeDto typeDto =typeService.findById(id);
                    typeDto.setCategory(categoryDto);
                    typeService.update(typeDto);
                    categoryDto.getTypes().add(typeDto);
                }
            }
        }
    }
}
