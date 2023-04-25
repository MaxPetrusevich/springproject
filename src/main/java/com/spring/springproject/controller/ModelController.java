package com.spring.springproject.controller;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.service.interfaces.ModelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

import static com.spring.springproject.controller.Constants.*;


@Controller
public class ModelController {

    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping(MODEL_LIST)
    public String findAll(Model model) {
        model.addAttribute(LIST, modelService.findAll());
        return MOD_LIST;
    }

    @GetMapping(MODEL_EDIT)
    public String editRedirect(Model model, HttpServletRequest request) {
        String modelId = request.getParameter(MODEL_ID);
        ModelDto modelDto = null;
        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            modelDto = modelService.findById(id);
        }
        model.addAttribute(UNIT, modelDto);
        return MOD_EDIT;
    }

    @PostMapping(MODEL_EDIT)
    public String edit(HttpServletRequest request, Model model) {
        String modelId = request.getParameter(MODEL_ID);
        ModelDto modelDto = null;
        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            modelDto = modelService.findById(id);
        }
        if (modelDto != null) {
            modelDto.setName(request.getParameter(NAME));
            modelService.update(modelDto);
        }
        return findAll(model);
    }

    @PostMapping(MODEL_DELETE)
    public String delete(HttpServletRequest request, Model model) {
        String modelId = request.getParameter(MODEL_ID);

        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            modelService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping(MODEL_ADD)
    public String add() {
        return MODEL_MODEL_ADD;
    }

    @PostMapping(MODEL_ADD)
    public String add(HttpServletRequest request, Model model) {
        ModelDto modelDto = new ModelDto();
        modelDto.setName(request.getParameter(NAME));
        modelService.save(modelDto);
        return findAll(model);
    }
}
