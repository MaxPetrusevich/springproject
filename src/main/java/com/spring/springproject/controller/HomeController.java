package com.spring.springproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.spring.springproject.controller.Constants.*;

@Controller
public class HomeController {

    private final CategoryController categoryController;
    private final ModelController modelController;
    private final ProducerController producerController;
    private final StoreController storeController;
    private final TechniqueController techniqueController;
    private final TypeController typeController;

    @Autowired
    public HomeController(CategoryController categoryController, ModelController modelController, ProducerController producerController, StoreController storeController, TechniqueController techniqueController, TypeController typeController) {
        this.categoryController = categoryController;
        this.modelController = modelController;
        this.producerController = producerController;
        this.storeController = storeController;
        this.techniqueController = techniqueController;
        this.typeController = typeController;
    }

    @GetMapping(START_PAGE)
    public String home() {
        return HOME_PAGE;
    }

    @GetMapping(HOME)
    public String goHome() {
        return HOME_PAGE;
    }

    @GetMapping(HOME_VIEW)
    public String view(@RequestParam(LIST_NAME) String listName, Model model) {
        int choose = Integer.parseInt(listName);
        String address = null;
        switch (choose) {
            case 1:
                address = "redirect:" + TECHNIQUES;
                break;
            case 2:
                address = "redirect:" + CATEGORIES_URL;
                break;
            case 3:
                address = "redirect:" + MODELS_URL;
                break;
            case 4:
                address = "redirect:" + PRODUCERS_URL;
                break;
            case 5:
                address = "redirect:" + TYPES_URL;
                break;
            case 6:
                address = "redirect:" + STORES_URL;
                break;
        }
        return address;
    }
}
