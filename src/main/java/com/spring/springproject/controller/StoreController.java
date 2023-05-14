package com.spring.springproject.controller;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.service.interfaces.StoreService;
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
public class StoreController {


    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @RequestMapping(STORES_URL)
    public String findAll(Model model,
                          @RequestParam(defaultValue = "3", required = false) int size,
                          @RequestParam(defaultValue = "1", required = false) int page,
                          @RequestParam(defaultValue = "", required = false) String name,
                          @RequestParam(defaultValue = "", required = false) String address) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<StoreDto> storePage = storeService.findAll(pageable, name, address);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("totalPage", storePage.getTotalPages());
        model.addAttribute(LIST, storePage.getContent());
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
        storeService.update(id, name, address);
        return "redirect:" + STORES_URL;
    }

    @PostMapping(DEL_STORE)
    public String delete(@RequestParam(STORE_ID) Integer id, Model model) {
        storeService.delete(id);
        return "redirect:" + STORES_URL;
    }

    @GetMapping(NEW_STORE)
    public String add() {
        return ST_ADD;
    }

    @PostMapping(NEW_STORE)
    public String add(@RequestParam(NAME) String name,
                      @RequestParam(ADDRESS) String address, Model model) {

        storeService.save(name, address);
        return "redirect:" + STORES_URL;
    }

}
