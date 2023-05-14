package com.spring.springproject.controller;

import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.TypeService;
import com.spring.springproject.specifications.TypeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;


import java.util.Set;
import java.util.stream.Collectors;

import static com.spring.springproject.controller.Constants.*;

@Controller
public class TypeController {


    private final TypeService typeService;

    @Autowired
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @RequestMapping(TYPES_URL)
    public String findAll(Model model,
                          @RequestParam(required = false, defaultValue = "") String name,
                          @RequestParam(required = false, defaultValue = "1") int page,
                          @RequestParam(required = false, defaultValue = "3") int size) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<TypeDto> typePage = typeService.findAll(pageable, name);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("name", name);
        model.addAttribute("totalPage", typePage.getTotalPages());
        model.addAttribute(LIST, typePage.getContent());
        return TY_LIST;
    }

    @GetMapping(TYPE)
    public String typeRedirect(Model model, @RequestParam(TYPE_ID) Integer id) {
        TypeDto typeDto = typeService.findById(id);
        model.addAttribute(UNIT, typeDto);
        return TY_EDIT;
    }

    @PostMapping(TYPE)
    public String edit(@RequestParam(TYPE_ID) Integer id, @RequestParam(NAME) String name, Model model) {
        typeService.update(id, name);
        return "redirect:" + TYPES_URL;
    }

    @PostMapping(DEL_TYPE)
    public String delete(@RequestParam(TYPE_ID) Integer id, Model model) {
        typeService.delete(id);
        return "redirect:" + TYPES_URL;
    }

    @GetMapping(NEW_TYPE)
    public String add() {
        return TY_ADD;
    }

    @PostMapping(NEW_TYPE)
    public String add(@RequestParam(NAME) String name) {
        typeService.save(name);
        return "redirect:" + TYPES_URL;
    }


}
