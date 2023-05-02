package com.spring.springproject.controller;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.service.interfaces.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import static com.spring.springproject.controller.Constants.*;


@Controller
public class ModelController {


    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping(MODELS_URL)
    public String findAll(Model model) {
        model.addAttribute(LIST, modelService.findAll());
        return MOD_LIST;
    }

    @GetMapping(MODEL_URL)
    public String editRedirect(Model model, @RequestParam(MODEL_ID) Integer id) {
        ModelDto modelDto = modelService.findById(id);

        model.addAttribute(UNIT, modelDto);
        return MOD_EDIT;
    }

    @PostMapping(MODEL_URL)
    public String edit(@RequestParam(MODEL_ID) Integer id, @RequestParam(NAME) String name, Model model) {
        modelService.update(id, name);
        return findAll(model);
    }

    @PostMapping(DEL_MODEL)
    public String delete(@RequestParam(MODEL_ID) Integer id, Model model) {

        modelService.delete(id);

        return findAll(model);
    }

    @GetMapping(NEW_MODEL)
    public String add() {
        return MODEL_MODEL_ADD;
    }

    @PostMapping(NEW_MODEL)
    public String add(@RequestParam(NAME) String name, Model model) {
        modelService.save(name);
        return findAll(model);
    }

    @PostMapping(MODEL_BY_NAME)
    public String findByName(Model model, @RequestParam(NAME) String name) {
        model.addAttribute(LIST, modelService.findByName(name));
        return MOD_LIST;
    }
}
