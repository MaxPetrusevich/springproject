package com.spring.springproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.spring.springproject.controller.Constants.*;

@Controller
public class HomeController {

   @GetMapping(START_PAGE)
    public String home() {
        return HOME_PAGE;
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/registration")
    public String register(){
        return "registration";
    }

    @GetMapping(HOME)
    public String goHome() {
        return HOME_PAGE;
    }

    @GetMapping(HOME_VIEW)
    public String view(@RequestParam(LIST_NAME) String listName) {
        int choose = Integer.parseInt(listName);
        String address = null;
        switch (choose) {
            case 1:
                address = REDIRECT + TECHNIQUES;
                break;
            case 2:
                address = REDIRECT + CATEGORIES_URL;
                break;
            case 3:
                address = REDIRECT + MODELS_URL;
                break;
            case 4:
                address = REDIRECT + PRODUCERS_URL;
                break;
            case 5:
                address = REDIRECT + TYPES_URL;
                break;
            case 6:
                address = REDIRECT + STORES_URL;
                break;
            case 7:
                address = REDIRECT + SWAGGER_UI_URL;
        }
        return address;
    }
}
