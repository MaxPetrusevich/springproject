package com.spring.springproject.controller;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.service.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
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

    @GetMapping(TECHNIQUE_LIST)
    public String findAll(Model model) {
        model.addAttribute(LIST, techniqueService.findAll());
        return T_LIST;
    }

    @GetMapping (TECHNIQUE_EDIT)
    public String editRedirect(Model model, HttpServletRequest request) {
        String techId = request.getParameter(TECH_ID);
        if (techId != null) {
            Integer id = Integer.parseInt(techId);
            model.addAttribute(UNIT, techniqueService.findById(id));
        }
        addAllAttributes(model);
        return T_EDIT;
    }

    @PostMapping(TECHNIQUE_EDIT)
    public String edit(Model model, HttpServletRequest request) {
        String techId = request.getParameter(TECH_ID);
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
        String producerId = request.getParameter(PRODUCER);
        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            techniqueDto.setProducer(producerService.findById(id));
        }
        String modelId = request.getParameter(MODEL);
        if (!StringUtils.isEmptyOrWhitespace(modelId)) {
            Integer id = Integer.parseInt(modelId);
            techniqueDto.setModel(modelService.findById(id));
        }
        String categoryId = request.getParameter(CATEGORY);
        if (!StringUtils.isEmptyOrWhitespace(categoryId)) {
            Integer id = Integer.parseInt(categoryId);
            techniqueDto.setCategory(categoryService.findById(id));
        }
        String price = request.getParameter(PRICE);
        if (!StringUtils.isEmptyOrWhitespace(price)) {
            techniqueDto.setPrice(Double.parseDouble(price));
        }
        String[] storeIdes = request.getParameterValues(STORE_ID);
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

    @RequestMapping(value = TECHNIQUE_DELETE, method = RequestMethod.POST)
    public String delete(Model model, HttpServletRequest request) {
        String techId = request.getParameter(TECH_ID);
        if (!StringUtils.isEmptyOrWhitespace(techId)) {
            Integer id = Integer.parseInt(techId);
            techniqueService.delete(id);
        }
        return findAll(model);
    }

    @PostMapping(TECHNIQUE_ADD)
    public String add(Model model, HttpServletRequest request) {
        TechniqueDto techniqueDto = new TechniqueDto();
        techniqueDto.setStoreList(new HashSet<>());
        convertEditAndAddParams(request, techniqueDto);
         techniqueService.save(techniqueDto);
        return findAll(model);
    }

    @GetMapping(TECHNIQUE_ADD)
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
