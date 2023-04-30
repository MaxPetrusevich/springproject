package com.spring.springproject.controller;

import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.service.interfaces.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.spring.springproject.controller.Constants.*;

@Controller
public class ProducerController {


    private final ProducerService producerService;

    @Autowired
    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping(PRODUCER_LIST)
    public String findAll(Model model) {
        model.addAttribute(LIST, producerService.findAll());
        return PROD_LIST;
    }

    @GetMapping(PRODUCER_EDIT)
    public String editRedirect(Model model, @RequestParam(PRODUCER_ID) Integer id) {
        ProducerDto producerDto = producerService.findById(id);
        model.addAttribute(UNIT, producerDto);
        return PROD_EDIT;
    }

    @PostMapping(PRODUCER_EDIT)
    public String edit(@RequestParam(PRODUCER_ID) Integer id, @RequestParam(NAME) String name,
                       @RequestParam(COUNTRY) String country, Model model) {
        ProducerDto producerDto = producerService.findById(id);

        if (producerDto != null) {
            producerDto.setName(name);
            producerDto.setCountry(country);
            producerService.update(producerDto);
        }
        return findAll(model);
    }

    @PostMapping(PRODUCER_DELETE)
    public String delete(@RequestParam(PRODUCER_ID) Integer id, Model model) {
        producerService.delete(id);
        return findAll(model);
    }

    @GetMapping(PRODUCER_ADD)
    public String add() {
        return PROD_ADD;
    }

    @PostMapping(PRODUCER_ADD)
    public String add(@RequestParam(NAME) String name, @RequestParam(COUNTRY) String country, Model model) {
        ProducerDto producerDto = new ProducerDto();
        producerDto.setName(name);
        producerDto.setCountry(country);
        producerService.save(producerDto);
        return findAll(model);
    }

    @PostMapping(PRODUCER_BY_NAME)
    public String findByName(Model model, @RequestParam(NAME) String name) {
        model.addAttribute(LIST, producerService.findByName(name));
        return PROD_LIST;
    }

    @PostMapping(PRODUCER_BY_COUNTRY)
    public String findByCountry(Model model, @RequestParam(COUNTRY) String country) {
        model.addAttribute(LIST, producerService.findByCountry(country));
        return PROD_LIST;
    }
}
