package com.spring.springproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/")
    public String home() {
        return "homePage.html";
    }

    @GetMapping("/home")
    public String goHome() {
        return "homePage.html";
    }

    @GetMapping("/homeView")
    public String view(HttpServletRequest request, Model model) {
        Integer choose = Integer.parseInt(request.getParameter("listName"));
        String address = null;
        switch (choose) {
            case 1:
                address = techniqueController.findAll(model);
                break;
            case 2:
                address = categoryController.findAll(model);
                break;
            case 3:
                address = modelController.findAll(model);
                break;
            case 4:
                address = producerController.findAll(model);
                break;
            case 5:
                address = typeController.findAll(model);
                break;
            case 6:
                address = storeController.findAll(model);
                break;
        }
        return address;
    }
}
