package com.spring.springproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

import static com.spring.springproject.controller.Constants.*;


@Controller
public class TechniqueController {


    private final TechniqueService techniqueService;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ModelService modelService;
    private final ProducerService producerService;


    @Autowired
    public TechniqueController(TechniqueService techniqueService, CategoryService categoryService, StoreService storeService, ModelService modelService, ProducerService producerService) {
        this.techniqueService = techniqueService;
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.modelService = modelService;
        this.producerService = producerService;
    }

    @RequestMapping(TECHNIQUES)
    public String findAll(Model model,
                          @RequestParam(defaultValue = "1", required = false) int page,
                          @RequestParam(defaultValue = "3", required = false) int size,
                          @RequestParam( required = false) Double startPrice,
                          @RequestParam( required = false) Double endPrice,
                          @RequestParam( required = false) String categoryName,
                          @RequestParam( required = false)String producerName,
                          @RequestParam( required = false)String modelName) throws JsonProcessingException {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<TechniqueDto> techniqueDtoPage = techniqueService.findAll(pageable, startPrice, endPrice, categoryName,producerName,modelName);
        model.addAttribute(PAGE, page);
        model.addAttribute(SIZE, size);
        model.addAttribute(START_PRICE, startPrice);
        model.addAttribute(END_PRICE, endPrice);
        model.addAttribute(TOTAL_PAGE, techniqueDtoPage.getTotalPages());
        model.addAttribute(LIST, techniqueDtoPage.getContent());
        return T_LIST;
    }

    @GetMapping(TECHNIQUE)
    public String editRedirect(Model model, @RequestParam(TECH_ID) Integer id) {
        model.addAttribute(UNIT, techniqueService.findById(id));
        addAllAttributes(model);
        return T_EDIT;
    }

    @PostMapping(TECHNIQUE)
    public String edit(@RequestParam(TECH_ID) Integer id,
                       @RequestParam(PRODUCER) Integer producerId, @RequestParam(MODEL) Integer modelId,
                       @RequestParam(CATEGORY) Integer categoryId, @RequestParam(PRICE) Double price,
                       @RequestParam(name = STORE_ID, required = false) Integer[] storeIdes) {
        techniqueService.update(producerId, modelId, categoryId, price, storeIdes, id);
        return REDIRECT + TECHNIQUES;

    }


    @PostMapping(value = DEL_TECHNIQUE)
    public String delete(@RequestParam(TECH_ID) Integer id) {
        techniqueService.delete(id);
        return REDIRECT + TECHNIQUES;
    }

    @PostMapping(NEW_TECHNIQUE)
    public String add(@RequestParam(PRODUCER) Integer producerId, @RequestParam(MODEL) Integer modelId,
                      @RequestParam(CATEGORY) Integer categoryId, @RequestParam(PRICE) Double price,
                      @RequestParam(name = STORE_ID, required = false) Integer[] storeIdes) {
        techniqueService.save(producerId, modelId, categoryId, price, storeIdes);
        return REDIRECT + TECHNIQUES;
    }

    @GetMapping(NEW_TECHNIQUE)
    public String add(Model model) {
        addAllAttributes(model);
        return T_ADD;
    }

    @PostMapping("/admin/import-json")
    @ResponseBody
    public ResponseEntity<String> importJsonData(@RequestParam("file") MultipartFile file) {
        try {
            techniqueService.importDataFromJson(file);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
    private void addAllAttributes(Model model) {
        model.addAttribute(CATEGORIES, categoryService.findAll());
        model.addAttribute(MODELS, modelService.findAll());
        model.addAttribute(PRODUCERS, producerService.findAll());
        model.addAttribute(STORES, storeService.findAll());
    }
    @GetMapping("/admin/downloadFile")
    public ResponseEntity<InputStreamResource> downloadFile() throws IOException {
        techniqueService.saveDataToJsonFile();
        // Замените путь к файлу на реальный путь на вашем сервере
        String filePath = "data.json";
        File file = new File(filePath);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.zip");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @PostMapping("/admin/techniques/image")
    public String uploadTechnique( @RequestParam("image") MultipartFile multipartFile, @RequestParam("techId")Integer techId){
        techniqueService.updateImage(techId, multipartFile);
        return "redirect:/admin/techniques";
    }
    @GetMapping("/admin/techniques/image")
    public String toUploadTechnique(@RequestParam("techId")Integer techId, Model model){
        model.addAttribute("techId",techId);
        return "tech/techImage";
    }
}
