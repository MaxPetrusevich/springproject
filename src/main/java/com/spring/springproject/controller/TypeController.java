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

import static com.spring.springproject.controller.Constants.*;

@Controller
public class TypeController {

    private final TypeService typeService;

    @Autowired
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping(TYPE_LIST)
    public String findAll(Model model) {
        model.addAttribute(LIST, typeService.findAll());
        return TY_LIST;
    }

    @GetMapping(TYPE_EDIT)
    public String typeRedirect(Model model, HttpServletRequest request) {
        String typeId = request.getParameter(TYPE_ID);
        TypeDto typeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(typeId)) {
            Integer id = Integer.parseInt(typeId);
            typeDto = typeService.findById(id);
        }
        model.addAttribute(UNIT, typeDto);
        return TY_EDIT;
    }

    @PostMapping(TYPE_EDIT)
    public String edit(HttpServletRequest request, Model model) {
        String typeId = request.getParameter(TYPE_ID);
        TypeDto typeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(typeId)) {
            Integer id = Integer.parseInt(typeId);
            typeDto = typeService.findById(id);
        }
        if (typeDto != null) {
            typeDto.setName(request.getParameter(NAME));
            typeService.update(typeDto);
        }
        return findAll(model);
    }

    @PostMapping(TYPE_DELETE)
    public String delete(HttpServletRequest request, Model model) {
        String typeId = request.getParameter(TYPE_ID);

        if (!StringUtils.isEmptyOrWhitespace(typeId)) {
            Integer id = Integer.parseInt(typeId);
            typeService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping(TYPE_ADD)
    public String add() {
        return TY_ADD;
    }

    @PostMapping(TYPE_ADD)
    public String add(HttpServletRequest request, Model model) {
        TypeDto typeDto = new TypeDto();
        typeDto.setName(request.getParameter(NAME));
        typeService.save(typeDto);
        return findAll(model);
    }
}
