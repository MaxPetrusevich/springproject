package com.spring.springproject.controller;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.ModelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;


@Controller
public class ModelController {
    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }
    @GetMapping("/model-list")
    public String findAll(Model model) {
        model.addAttribute("list", modelService.findAll());
        return "model/modelList";
    }

    @GetMapping("/model-edit")
    public String editRedirect(Model model, HttpServletRequest request) {
        String modelId = request.getParameter("modelId");
        ModelDto modelDto = null;
        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            modelDto = modelService.findById(id);
        }
        model.addAttribute("unit", modelDto);
        return "model/modelEdit";
    }

    @PostMapping("/model-edit")
    public String edit(HttpServletRequest request, Model model) {
        String modelId = request.getParameter("modelId");
        ModelDto modelDto = null;
        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            modelDto = modelService.findById(id);
        }
        modelDto.setName(request.getParameter("name"));
        modelService.update(modelDto);
        return findAll(model);
    }

    @PostMapping("/model-delete")
    public String delete(HttpServletRequest request, Model model){
        String modelId = request.getParameter("modelId");

        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            modelService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping("/model-add")
    public String add(){
        return "/model/modelAdd";
    }
    @PostMapping("/model-add")
    public String add(HttpServletRequest request, Model model){
        ModelDto modelDto = new ModelDto();
        modelDto.setName(request.getParameter("name"));
        modelDto = modelService.save(modelDto);
        return findAll(model);
    }
}
