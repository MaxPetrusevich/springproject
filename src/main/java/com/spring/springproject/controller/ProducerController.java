package com.spring.springproject.controller;

import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.service.interfaces.ProducerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

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
    public String editRedirect(Model model, HttpServletRequest request) {
        String producerId = request.getParameter(PRODUCER_ID);
        ProducerDto producerDto = null;
        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            producerDto = producerService.findById(id);
        }
        model.addAttribute(UNIT, producerDto);
        return PROD_EDIT;
    }

    @PostMapping(PRODUCER_EDIT)
    public String edit(HttpServletRequest request, Model model) {
        String producerId = request.getParameter(PRODUCER_ID);
        ProducerDto producerDto = null;
        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            producerDto = producerService.findById(id);
        }
        if (producerDto != null) {
            producerDto.setName(request.getParameter(NAME));
            producerDto.setCountry(request.getParameter(COUNTRY));
            producerService.update(producerDto);
        }
        return findAll(model);
    }

    @PostMapping(PRODUCER_DELETE)
    public String delete(HttpServletRequest request, Model model) {
        String producerId = request.getParameter(PRODUCER_ID);

        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            producerService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping(PRODUCER_ADD)
    public String add() {
        return PROD_ADD;
    }

    @PostMapping(PRODUCER_ADD)
    public String add(HttpServletRequest request, Model model) {
        ProducerDto producerDto = new ProducerDto();
        producerDto.setName(request.getParameter(NAME));
        producerDto.setCountry(request.getParameter(COUNTRY));
        producerService.save(producerDto);
        return findAll(model);
    }

    @PostMapping(PRODUCER_BY_NAME)
    public String findByName(Model model, HttpServletRequest request){
        String name = request.getParameter(NAME);
        model.addAttribute(LIST, producerService.findByName(name));
        return PROD_LIST;
    }
    @PostMapping(PRODUCER_BY_COUNTRY)
    public String findByCountry(Model model, HttpServletRequest request){
        String country = request.getParameter(COUNTRY);
        model.addAttribute(LIST, producerService.findByCountry(country));
        return PROD_LIST;
    }
}
