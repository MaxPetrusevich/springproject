package com.spring.springproject.controller;

import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import static com.spring.springproject.controller.Constants.*;

@Controller
public class TypeController {


    private final TypeService typeService;

    @Autowired
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping(TYPES_URL)
    public String findAll(Model model) {
        model.addAttribute(LIST, typeService.findAll());
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
        TypeDto typeDto = typeService.findById(id);
        if (typeDto != null) {
            typeDto.setName(name);
            typeService.update(typeDto);
        }
        return findAll(model);
    }

    @PostMapping(DEL_TYPE)
    public String delete(@RequestParam(TYPE_ID) Integer id, Model model) {
        typeService.delete(id);
        return findAll(model);
    }

    @GetMapping(NEW_TYPE)
    public String add() {
        return TY_ADD;
    }

    @PostMapping(NEW_TYPE)
    public String add(@RequestParam(NAME) String name, Model model) {
        TypeDto typeDto = new TypeDto();
        typeDto.setName(name);
        typeService.save(typeDto);
        return findAll(model);
    }

    @PostMapping(TYPE_BY_NAME)
    public String findByName(Model model, @RequestParam(NAME) String name) {
        model.addAttribute(LIST, typeService.findByName(name));
        return TY_LIST;
    }
}
