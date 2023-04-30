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

    @GetMapping(CATEGORY_LIST_MAP)
    public String findAll(Model model) {
        model.addAttribute(LIST, categoryService.findAll());
        return CATEGORY_LIST;
    }

    @GetMapping(CATEGORY_EDIT)
    public String editRedirect(Model model, @RequestParam(CAT_ID) Integer id) {
        CategoryDto categoryDto = categoryService.findById(id);
        model.addAttribute(UNIT, categoryDto);
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_EDIT;
    }

    @PostMapping(CATEGORY_EDIT)
    public String edit(@RequestParam(CAT_ID) Integer id, @RequestParam(TYPE_ID) Integer[] typeIdes, @RequestParam(NAME) String name, Model model) {
        CategoryDto categoryDto = categoryService.findById(id);
        if (categoryDto != null) {
            setEditParams(name, typeIdes, categoryDto);
            categoryService.update(categoryDto);
        }
        return findAll(model);
    }

    @PostMapping(CATEGORY_DELETE)
    public String delete(@RequestParam(CAT_ID) Integer id, Model model) {
         for (TypeDto type :
                    categoryService.findById(id).getTypes()) {
                type.setCategory(null);
                typeService.update(type);
            }
            categoryService.delete(id);
        return findAll(model);
    }

    @GetMapping(CATEGORY_ADD)
    public String add(Model model) {
        model.addAttribute(TYPES, typeService.findAll());
        return CAT_ADD;
    }

    @PostMapping(CATEGORY_ADD)
    public String add(@RequestParam(NAME) String name, Model model, @RequestParam(TYPE_ID) Integer[] typeIdes) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setTypes(new HashSet<>());
        categoryDto = categoryService.save(categoryDto);
        setEditParams(name, typeIdes, categoryDto);
        categoryService.update(categoryDto);
        return findAll(model);
    }

    private void setEditParams(String name, Integer[] typeIdes, CategoryDto categoryDto) {
        categoryDto.setName(name);
        for (TypeDto type :
                categoryDto.getTypes()) {
            type.setCategory(null);
            typeService.update(type);
        }
        if (categoryDto.getTypes() != null) {
            categoryDto.getTypes().removeAll(categoryDto.getTypes());
        }
        for (Integer typeId :
                typeIdes) {
            TypeDto typeDto = typeService.findById(typeId);
            typeDto.setCategory(categoryDto);
            typeService.update(typeDto);
            categoryDto.getTypes().add(typeDto);
        }
    }
}

