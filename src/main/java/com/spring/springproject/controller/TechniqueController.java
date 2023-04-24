package com.spring.springproject.controller;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.service.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.HashSet;


@Controller
public class TechniqueController {
    private final TechniqueService techniqueService;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ModelService modelService;
    private final ProducerService producerService;


    @Autowired
    public TechniqueController(TechniqueService techniqueService, CategoryService categoryService, StoreService storeService, ModelService modelService, ProducerService producerService) {
        this.techniqueService = techniqueService;
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.modelService = modelService;
        this.producerService = producerService;
    }

    @GetMapping("/technique-list")
    public String findAll(Model model) {
        model.addAttribute("list", techniqueService.findAll());
        return "tech/techList";
    }

    @GetMapping ("/technique-edit")
    public String editRedirect(Model model, HttpServletRequest request) {
        String techId = request.getParameter("techId");
        if (techId != null) {
            Integer id = Integer.parseInt(techId);
            model.addAttribute("unit", techniqueService.findById(id));
        }
        addAllAttibutes(model);
        return "tech/techEdit";
    }

    @PostMapping( "/technique-edit")
    public String edit(Model model, HttpServletRequest request) {
        String techId = request.getParameter("techId");
        TechniqueDto techniqueDto = null;
        if (!StringUtils.isEmptyOrWhitespace(techId)) {
            Integer id = Integer.parseInt(techId);
            techniqueDto = techniqueService.findById(id);
        }
        convertEditAndAddParams(request, techniqueDto);

        techniqueService.update(techniqueDto);
        return findAll(model);
    }

    private void convertEditAndAddParams(HttpServletRequest request, TechniqueDto techniqueDto) {
        String producerId = request.getParameter("producer");
        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            techniqueDto.setProducer(producerService.findById(id));
        }
        String modelId = request.getParameter("model");
        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            techniqueDto.setModel(modelService.findById(id));
        }
        String categoryId = request.getParameter("category");
        if (!StringUtils.isEmptyOrWhitespace(categoryId)) {
            Integer id = Integer.parseInt(categoryId);
            techniqueDto.setCategory(categoryService.findById(id));
        }
        String price = request.getParameter("price");
        if (!StringUtils.isEmptyOrWhitespace(price)) {
            techniqueDto.setPrice(Double.parseDouble(price));
        }
        String[] storeIdes = request.getParameterValues("storeId");
        techniqueDto.getStoreList().removeAll(techniqueDto.getStoreList());
        if (storeIdes != null) {
            for (String storeId :
                    storeIdes) {
                if (!StringUtils.isEmptyOrWhitespace(storeId)) {
                    Integer id = Integer.parseInt(storeId);
                    techniqueDto.getStoreList().add(storeService.findById(id));
                }
            }
        }
    }

    @RequestMapping(value = "/technique-delete", method = RequestMethod.POST)
    public String delete(Model model, HttpServletRequest request) {
        String techId = request.getParameter("techId");
        if (!StringUtils.isEmptyOrWhitespace(techId)) {
            Integer id = Integer.parseInt(techId);
            techniqueService.delete(id);
        }
        return findAll(model);
    }

    @PostMapping("/technique-add")
    public String add(Model model, HttpServletRequest request) {
        TechniqueDto techniqueDto = new TechniqueDto();
        techniqueDto.setStoreList(new HashSet<>());
        convertEditAndAddParams(request, techniqueDto);
        techniqueDto = techniqueService.save(techniqueDto);
        return findAll(model);
    }

    @GetMapping("/technique-add")
    public String add(Model model) {
        addAllAttibutes(model);
        return "tech/techAdd";
    }

    private void addAllAttibutes(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("models", modelService.findAll());
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("stores", storeService.findAll());
    }


}
