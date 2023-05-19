package com.spring.springproject.controller;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.service.interfaces.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.spring.springproject.controller.Constants.*;


@Controller
public class ModelController {


    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @RequestMapping(MODELS_URL)
    public String findAll(Model model,
                          @RequestParam(required = false, defaultValue = "") String name,
                          @RequestParam(required = false, defaultValue = "1") int page,
                          @RequestParam(required = false, defaultValue = "3") int size) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<ModelDto> modelPage = modelService.findAll(pageable, name);
        model.addAttribute(PAGE, page);
        model.addAttribute(SIZE, size);
        model.addAttribute(NAME, name);
        model.addAttribute(TOTAL_PAGE, modelPage.getTotalPages());
        model.addAttribute(LIST, modelPage.getContent());
        return MOD_LIST;
    }

    @GetMapping(MODEL_URL)
    public String editRedirect(Model model, @RequestParam(MODEL_ID) Integer id) {
        ModelDto modelDto = modelService.findById(id);
        model.addAttribute(UNIT, modelDto);
        return MOD_EDIT;
    }

    @PostMapping(MODEL_URL)
    public String edit(@RequestParam(MODEL_ID) Integer id, @RequestParam(NAME) String name) {
        modelService.update(id, name);
        return REDIRECT + MODELS_URL;
    }

    @PostMapping(DEL_MODEL)
    public String delete(@RequestParam(MODEL_ID) Integer id) {
        modelService.delete(id);
        return REDIRECT + MODELS_URL;
    }

    @GetMapping(NEW_MODEL)
    public String add() {
        return MODEL_MODEL_ADD;
    }

    @PostMapping(NEW_MODEL)
    public String add(@RequestParam(NAME) String name) {
        modelService.save(name);
        return REDIRECT + MODELS_URL;
    }

}
