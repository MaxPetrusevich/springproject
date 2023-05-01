package com.spring.springproject.controller;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.service.interfaces.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.spring.springproject.controller.Constants.*;


@Controller
public class StoreController {


    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping(STORES_URL)
    public String findAll(Model model) {
        model.addAttribute(LIST, storeService.findAll());
        return ST_LIST;
    }

    @GetMapping(STORE)
    public String typeRedirect(Model model, @RequestParam(STORE_ID) Integer id) {
        StoreDto storeDto = storeService.findById(id);
        model.addAttribute(UNIT, storeDto);
        return ST_EDIT;
    }

    @PostMapping(STORE)
    public String edit(@RequestParam(STORE_ID) Integer id, @RequestParam(NAME) String name,
                       @RequestParam(ADDRESS) String address, Model model) {
        StoreDto storeDto = storeService.findById(id);
        if (storeDto != null) {
            storeDto.setName(name);
            storeDto.setAddress(address);
            storeService.update(storeDto);
        }
        return findAll(model);
    }

    @PostMapping(DEL_STORE)
    public String delete(@RequestParam(STORE_ID) Integer id, Model model) {
        storeService.delete(id);
        return findAll(model);
    }

    @GetMapping(NEW_STORE)
    public String add() {
        return ST_ADD;
    }

    @PostMapping(NEW_STORE)
    public String add(@RequestParam(NAME) String name,
                      @RequestParam(ADDRESS) String address, Model model) {
        StoreDto storeDto = new StoreDto();
        storeDto.setName(name);
        storeDto.setAddress(address);
        storeService.save(storeDto);
        return findAll(model);
    }

    @PostMapping(STORE_BY_NAME)
    public String findByName(Model model, @RequestParam(NAME) String name) {
        model.addAttribute(LIST, storeService.findByName(name));
        return ST_LIST;
    }

    @PostMapping(STORE_BY_ADDRESS)
    public String findByCountry(Model model, @RequestParam(ADDRESS) String address) {
        model.addAttribute(LIST, storeService.findByAddress(address));
        return ST_LIST;
    }
}
