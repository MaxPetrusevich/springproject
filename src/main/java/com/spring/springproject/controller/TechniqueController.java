package com.spring.springproject.controller;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.spring.springproject.controller.Constants.*;

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

    @GetMapping(TECHNIQUES)
    public String findAll(Model model) {
        model.addAttribute(LIST, techniqueService.findAll());
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
        TechniqueDto techniqueDto = techniqueService.findById(id);
        convertEditAndAddParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        techniqueService.update(techniqueDto);
        return findAll(model);

    }

    @PostMapping(TECHNIQUE_BY_PRICE)
    public String findByPrice(Model model, @RequestParam(START_PRICE) Double startPrice,
                              @RequestParam(END_PRICE) Double endPrice) {
        model.addAttribute(LIST, techniqueService.findByPriceBetween(startPrice, endPrice));
        return T_LIST;
    }

    private void convertEditAndAddParams(Integer producerId, Integer modelId, Integer categoryId,
                                         Double price, Integer[] storeIdes, TechniqueDto techniqueDto) {

        techniqueDto.setProducer(producerService.findById(producerId));
        techniqueDto.setModel(modelService.findById(modelId));
        techniqueDto.setCategory(categoryService.findById(categoryId));
        techniqueDto.setPrice(price);
        techniqueDto.getStoreList().removeAll(techniqueDto.getStoreList());
        for (Integer storeId :
                storeIdes) {
            techniqueDto.getStoreList().add(storeService.findById(storeId));
        }
    }


    @PostMapping(value = DEL_TECHNIQUE)
    public String delete(Model model, @RequestParam(TECH_ID) Integer id) {
        techniqueService.delete(id);
        return findAll(model);
    }

    @PostMapping(NEW_TECHNIQUE)
    public String add(Model model,
                      @RequestParam(PRODUCER) Integer producerId, @RequestParam(MODEL) Integer modelId,
                      @RequestParam(CATEGORY) Integer categoryId, @RequestParam(PRICE) Double price,
                      @RequestParam(STORE_ID) Integer[] storeIdes) {
        TechniqueDto techniqueDto = new TechniqueDto();
        techniqueDto.setStoreList(new HashSet<>());
        convertEditAndAddParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        techniqueService.save(techniqueDto);
        return findAll(model);
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
