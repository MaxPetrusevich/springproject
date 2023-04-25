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

import static com.spring.springproject.controller.Constants.*;


@Controller
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping(STORE_LIST)
    public String findAll(Model model) {
        model.addAttribute(LIST, storeService.findAll());
        return ST_LIST;
    }

    @GetMapping(STORE_EDIT)
    public String typeRedirect(Model model, HttpServletRequest request) {
        String storeId = request.getParameter(STORE_EDIT);
        StoreDto storeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(storeId)) {
            Integer id = Integer.parseInt(storeId);
            storeDto = storeService.findById(id);
        }
        model.addAttribute(UNIT, storeDto);
        return ST_EDIT;
    }

    @PostMapping(STORE_EDIT)
    public String edit(HttpServletRequest request, Model model) {
        String storeId = request.getParameter(STORE_ID);
        StoreDto storeDto = null;
        if (!StringUtils.isEmptyOrWhitespace(storeId)) {
            Integer id = Integer.parseInt(storeId);
            storeDto = storeService.findById(id);
        }
        if (storeDto != null) {
            storeDto.setName(request.getParameter(NAME));
            storeDto.setAddress(request.getParameter(ADDRESS));
            storeService.update(storeDto);
        }
        return findAll(model);
    }

    @PostMapping(STORE_DELETE)
    public String delete(HttpServletRequest request, Model model) {
        String storeId = request.getParameter(STORE_ID);

        if (!StringUtils.isEmptyOrWhitespace(storeId)) {
            Integer id = Integer.parseInt(storeId);
            storeService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping(STORE_ADD)
    public String add() {
        return ST_ADD;
    }

    @PostMapping(STORE_ADD)
    public String add(HttpServletRequest request, Model model) {
        StoreDto storeDto = new StoreDto();
        storeDto.setName(request.getParameter(NAME));
        storeDto.setAddress(request.getParameter(ADDRESS));
        storeService.save(storeDto);
        return findAll(model);
    }
}
