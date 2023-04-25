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

import static com.spring.springproject.controller.Constants.*;

@Controller
public class CategoryController {

    private final CategoryService categoryService;
    private final TypeService typeService;

    @Autowired
    public CategoryController(CategoryService categoryService, TypeService typeService) {
        this.categoryService = categoryService;
        this.typeService = typeService;
    }

    @GetMapping(CATEGORY_LIST_MAP)
    public String findAll(Model model) {
        model.addAttribute(LIST, categoryService.findAll());
        return CATEGORY_LIST;
    }

    @GetMapping(CATEGORY_EDIT)
    public String editRedirect(Model model, HttpServletRequest request) {
        String catId = request.getParameter(CAT_ID);
        CategoryDto categoryDto = null;
        if (!StringUtils.isEmptyOrWhitespace(catId)) {
            Integer id = Integer.parseInt(catId);
            categoryDto = categoryService.findById(id);
        }
        model.addAttribute(UNIT, categoryDto);
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_EDIT;
    }

    @PostMapping(CATEGORY_EDIT)
    public String edit(HttpServletRequest request, Model model) {
        String catId = request.getParameter(CAT_ID);
        CategoryDto categoryDto = null;
        if (!StringUtils.isEmptyOrWhitespace(catId)) {
            Integer id = Integer.parseInt(catId);
            categoryDto = categoryService.findById(id);
        }
        if (categoryDto != null) {
            if (categoryDto.getTypes() != null) {
                categoryDto.getTypes().removeAll(categoryDto.getTypes());
            }
            setEditParams(request, categoryDto);
            categoryService.update(categoryDto);
        }
        return findAll(model);
    }

    @PostMapping(CATEGORY_DELETE)
    public String delete(HttpServletRequest request, Model model) {
        String catId = request.getParameter(CAT_ID);
        if (!StringUtils.isEmptyOrWhitespace(catId)) {
            Integer id = Integer.parseInt(catId);
            categoryService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping(CATEGORY_ADD)
    public String add(Model model) {
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_ADD;
    }

    @PostMapping(CATEGORY_ADD)
    public String add(HttpServletRequest request, Model model) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setTypes(new HashSet<>());
        categoryDto = categoryService.save(categoryDto);
        setEditParams(request, categoryDto);
        categoryService.update(categoryDto);
        return findAll(model);
    }

    private void setEditParams(HttpServletRequest request, CategoryDto categoryDto) {
        categoryDto.setName(request.getParameter(NAME));
        String[] typeIdes = request.getParameterValues(TYPE_ID);
        if (typeIdes != null) {
            for (String typeId :
                    typeIdes) {
                if (!StringUtils.isEmptyOrWhitespace(typeId)) {
                    Integer id = Integer.parseInt(typeId);
                    TypeDto typeDto = typeService.findById(id);
                    typeDto.setCategory(categoryDto);
                    typeService.update(typeDto);
                    categoryDto.getTypes().add(typeDto);
                }
            }
        }
    }
}
