package com.spring.springproject.controller;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.service.interfaces.CategoryService;
import com.spring.springproject.service.interfaces.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @RequestMapping(CATEGORIES_URL)
    public String findAll(Model model, @RequestParam(required = false, defaultValue = "") String name, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "3") int size) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<CategoryDto> categoryPage = categoryService.findAll(pageable, name);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("name", name);
        model.addAttribute("totalPage", categoryPage.getTotalPages());
        model.addAttribute(LIST, categoryPage.getContent());
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
        categoryService.save(name, typeIdes, categoryDto);
        return "redirect:" + CATEGORIES_URL;
    }

    @PostMapping(DEL_CATEGORY)
    public String delete(@RequestParam(CAT_ID) Integer id) {
        categoryService.delete(id);
        return "redirect:" + CATEGORIES_URL;
    }

    @GetMapping(NEW_CATEGORY)
    public String add(Model model) {
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_ADD;
    }

    @PostMapping(NEW_CATEGORY)
    public String add(@RequestParam(NAME) String name,@RequestParam(name = TYPE_ID, required = false, defaultValue = "") Integer[] typeIdes) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setTypes(new HashSet<>());
        categoryService.save(name, typeIdes, categoryDto);
        return "redirect:" + CATEGORIES_URL;
    }
}

