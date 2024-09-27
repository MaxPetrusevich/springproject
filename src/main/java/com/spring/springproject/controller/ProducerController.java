package com.spring.springproject.controller;

import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.service.interfaces.ProducerService;
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
public class ProducerController {


    private final ProducerService producerService;

    @Autowired
    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @RequestMapping(PRODUCERS_URL)
    public String findAll(Model model,
                          @RequestParam(defaultValue = "3", required = false) int size,
                          @RequestParam(defaultValue = "1", required = false) int page,
                          @RequestParam(defaultValue = "", required = false) String name,
                          @RequestParam(defaultValue = "", required = false) String country) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<ProducerDto> producerPage = producerService.findAll(pageable, name, country);
        model.addAttribute(PAGE, page);
        model.addAttribute(SIZE, size);
        model.addAttribute(NAME, name);
        model.addAttribute(COUNTRY, country);
        model.addAttribute(TOTAL_PAGE, producerPage.getTotalPages());
        model.addAttribute(LIST, producerPage.getContent());
        return PROD_LIST;
    }

    @GetMapping(PRODUCER_URL)
    public String editRedirect(Model model, @RequestParam(PRODUCER_ID) Integer id) {
        ProducerDto producerDto = producerService.findById(id);
        model.addAttribute(UNIT, producerDto);
        return PROD_EDIT;
    }

    @PostMapping(PRODUCER_URL)
    public String edit(@RequestParam(PRODUCER_ID) Integer id, @RequestParam(NAME) String name,
                       @RequestParam(COUNTRY) String country) {
        producerService.update(id, name, country);
        return REDIRECT + PRODUCERS_URL;
    }

    @PostMapping(DEL_PRODUCER)
    public String delete(@RequestParam(PRODUCER_ID) Integer id) {
        producerService.delete(id);
        return REDIRECT + PRODUCERS_URL;
    }

    @GetMapping(NEW_PRODUCER)
    public String add() {
        return PROD_ADD;
    }

    @PostMapping(NEW_PRODUCER)
    public String add(@RequestParam(NAME) String name, @RequestParam(COUNTRY) String country) {
        producerService.save(name, country);
        return REDIRECT + PRODUCERS_URL;
    }


}
