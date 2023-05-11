package com.spring.springproject.controller;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.*;
import liquibase.pro.packaged.D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.spring.springproject.controller.Constants.*;

import java.util.HashSet;
import java.util.Set;


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

    @RequestMapping(TECHNIQUES)
    public String findAll(Model model,
                          @RequestParam(defaultValue = "1", required = false) int page,
                          @RequestParam(defaultValue = "3", required = false) int size,
                          @RequestParam(defaultValue = "", required = false) Double startPrice,
                          @RequestParam(defaultValue = "", required = false) Double endPrice) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page-1);
        Page<TechniqueDto> techniqueDtoPage = techniqueService.findAll(pageable, startPrice, endPrice);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("startPrice", startPrice);
        model.addAttribute("endPrice", endPrice);
        model.addAttribute("totalPage", techniqueDtoPage.getTotalPages());
        model.addAttribute(LIST, techniqueDtoPage.getContent());
        return T_LIST;
    }

    @GetMapping(TECHNIQUE)
    public String editRedirect(Model model, @RequestParam(TECH_ID) Integer id) {
        model.addAttribute(UNIT, techniqueService.findById(id));
        addAllAttributes(model);
        return T_EDIT;
    }

    @PostMapping(TECHNIQUE)
    public String edit(Model model, @RequestParam(TECH_ID) Integer id,
                       @RequestParam(PRODUCER) Integer producerId, @RequestParam(MODEL) Integer modelId,
                       @RequestParam(CATEGORY) Integer categoryId, @RequestParam(PRICE) Double price,
                       @RequestParam(STORE_ID) Integer[] storeIdes) {
        techniqueService.update(producerId, modelId, categoryId, price, storeIdes, id);
        return "redirect:" + TECHNIQUES;

    }





    @PostMapping(value = DEL_TECHNIQUE)
    public String delete(Model model, @RequestParam(TECH_ID) Integer id) {
        techniqueService.delete(id);
        return "redirect:" + TECHNIQUES;
    }

    @PostMapping(NEW_TECHNIQUE)
    public String add(Model model,
                      @RequestParam(PRODUCER) Integer producerId, @RequestParam(MODEL) Integer modelId,
                      @RequestParam(CATEGORY) Integer categoryId, @RequestParam(PRICE) Double price,
                      @RequestParam(STORE_ID) Integer[] storeIdes) {
        techniqueService.save(producerId, modelId, categoryId, price, storeIdes);
        return "redirect:" + TECHNIQUES;
    }

    @GetMapping(NEW_TECHNIQUE)
    public String add(Model model) {
        addAllAttributes(model);
        return T_ADD;
    }

    private void addAllAttributes(Model model) {
        model.addAttribute(CATEGORIES, categoryService.findAll());
        model.addAttribute(MODELS, modelService.findAll());
        model.addAttribute(PRODUCERS, producerService.findAll());
        model.addAttribute(STORES, storeService.findAll());
    }


}
