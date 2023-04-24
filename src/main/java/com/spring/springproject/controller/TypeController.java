package com.spring.springproject.controller;

import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.TypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

@Controller
public class TypeController {
    private final TypeService typeService;

    @Autowired
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }
    @GetMapping("/type-list")
    public String findAll(Model model) {
        model.addAttribute("list", typeService.findAll());
        return "type/typeList";
    }

    @GetMapping("/type-edit")
    public String typeirect(Model model, HttpServletRequest request) {
        String typeId = request.getParameter("typeId");
        TypeDto typeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(typeId)) {
            Integer id = Integer.parseInt(typeId);
            typeDto = typeService.findById(id);
        }
        model.addAttribute("unit", typeDto);
        return "type/typeEdit";
    }

    @PostMapping("/type-edit")
    public String edit(HttpServletRequest request, Model model) {
        String typeId = request.getParameter("typeId");
        TypeDto typeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(typeId)) {
            Integer id = Integer.parseInt(typeId);
            typeDto = typeService.findById(id);
        }
        typeDto.setName(request.getParameter("name"));
        typeService.update(typeDto);
        return findAll(model);
    }

    @PostMapping("/type-delete")
    public String delete(HttpServletRequest request, Model model){
        String typeId = request.getParameter("typeId");

        if (!StringUtils.isEmptyOrWhitespace(typeId)) {
            Integer id = Integer.parseInt(typeId);
            typeService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping("/type-add")
    public String add(){
        return "/type/typeAdd";
    }
    @PostMapping("/type-add")
    public String add(HttpServletRequest request, Model model){
        TypeDto typeDto = new TypeDto();
        typeDto.setName(request.getParameter("name"));
        typeDto = typeService.save(typeDto);
        return findAll(model);
    }
}
