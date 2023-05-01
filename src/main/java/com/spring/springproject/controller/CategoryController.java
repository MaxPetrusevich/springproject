package com.spring.springproject.controller;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.CategoryService;
import com.spring.springproject.service.interfaces.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping(CATEGORY_BY_NAME)
    public String findByName(Model model, @RequestParam(NAME) String name) {
        model.addAttribute(LIST, categoryService.findByName(name));
        return CATEGORY_LIST;
    }

    @GetMapping(CATEGORIES_URL)
    public String findAll(Model model) {
        model.addAttribute(LIST, categoryService.findAll());
        return CATEGORY_LIST;
    }

    @GetMapping(CATEGORY_URL)
    public String editRedirect(Model model, @RequestParam(CAT_ID) Integer id) {
        CategoryDto categoryDto = categoryService.findById(id);
        model.addAttribute(UNIT, categoryDto);
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_EDIT;
    }

    @PostMapping(CATEGORY_URL)
    public String edit(@RequestParam(CAT_ID) Integer id, @RequestParam(TYPE_ID) Integer[] typeIdes, @RequestParam(NAME) String name, Model model) {
        CategoryDto categoryDto = categoryService.findById(id);
        if (categoryDto != null) {
            categoryService.save(name, typeIdes, categoryDto);
        }
        return findAll(model);
    }

    @PostMapping(DEL_CATEGORY)
    public String delete(@RequestParam(CAT_ID) Integer id, Model model) {
        categoryService.delete(id);
        return findAll(model);
    }

    @GetMapping(NEW_CATEGORY)
    public String add(Model model) {
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_ADD;
    }

    @PostMapping(NEW_CATEGORY)
    public String add(@RequestParam(NAME) String name, Model model, @RequestParam(TYPE_ID) Integer[] typeIdes) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setTypes(new HashSet<>());
        categoryService.save(name, typeIdes, categoryDto);
        return findAll(model);
    }
}

