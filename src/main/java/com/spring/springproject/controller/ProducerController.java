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

@Controller
public class ProducerController {
    private final ProducerService producerService;

    @Autowired
    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping("/producer-list")
    public String findAll(Model model) {
        model.addAttribute("list", producerService.findAll());
        return "producer/producerList";
    }

    @GetMapping("/producer-edit")
    public String editRedirect(Model model, HttpServletRequest request) {
        String producerId = request.getParameter("producerId");
        ProducerDto producerDto = null;
        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            producerDto = producerService.findById(id);
        }
        model.addAttribute("unit", producerDto);
        return "producer/producerEdit";
    }

    @PostMapping("/producer-edit")
    public String edit(HttpServletRequest request, Model model) {
        String producerId = request.getParameter("producerId");
        ProducerDto producerDto = null;
        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            producerDto = producerService.findById(id);
        }
        producerDto.setName(request.getParameter("name"));
        producerDto.setCountry(request.getParameter("country"));
        producerService.update(producerDto);
        return findAll(model);
    }

    @PostMapping("/producer-delete")
    public String delete(HttpServletRequest request, Model model){
        String producerId = request.getParameter("producerId");

        if (!StringUtils.isEmptyOrWhitespace(producerId)) {
            Integer id = Integer.parseInt(producerId);
            producerService.delete(id);
        }
        return findAll(model);
    }

    @GetMapping("/producer-add")
    public String add(){
        return "/producer/producerAdd";
    }
    @PostMapping("/producer-add")
    public String add(HttpServletRequest request, Model model){
        ProducerDto producerDto = new ProducerDto();
        producerDto.setName(request.getParameter("name"));
        producerDto.setCountry(request.getParameter("country"));
        producerDto = producerService.save(producerDto);
        return findAll(model);
    }
}
