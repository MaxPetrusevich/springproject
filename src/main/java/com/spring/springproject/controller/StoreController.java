package com.spring.springproject.controller;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.service.interfaces.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

@Controller
public class StoreController {
    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }
    @GetMapping("/store-list")
    public String findAll(Model model) {
        model.addAttribute("list", storeService.findAll());
        return "store/storeList";
    }

    @GetMapping("/store-edit")
    public String typeRedirect(Model model, HttpServletRequest request) {
        String storeId = request.getParameter("storeId");
        StoreDto storeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(storeId)) {
            Integer id = Integer.parseInt(storeId);
            storeDto = storeService.findById(id);
        }
        model.addAttribute("unit", storeDto);
        return "store/storeEdit";
    }

    @PostMapping("/store-edit")
    public String edit(HttpServletRequest request, Model model) {
        String storeId = request.getParameter("storeId");
        StoreDto storeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(storeId)) {
            Integer id = Integer.parseInt(storeId);
            storeDto = storeService.findById(id);
        }
        storeDto.setName(request.getParameter("name"));
        storeDto.setAddress(request.getParameter("address"));
        storeService.update(storeDto);
        return findAll(model);
    }

    @PostMapping("/store-delete")
    public String delete(HttpServletRequest request, Model model){
        String storeId = request.getParameter("storeId");

        if (!StringUtils.isEmptyOrWhitespace(storeId)) {
            Integer id = Integer.parseInt(storeId);
            storeService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping("/store-add")
    public String add(){
        return "/store/storeAdd";
    }
    @PostMapping("/store-add")
    public String add(HttpServletRequest request, Model model){
        StoreDto storeDto = new StoreDto();
        storeDto.setName(request.getParameter("name"));
        storeDto.setAddress(request.getParameter("address"));
        storeDto = storeService.save(storeDto);
        return findAll(model);
    }
}
