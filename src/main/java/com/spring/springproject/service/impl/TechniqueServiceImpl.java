package com.spring.springproject.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Model;
import com.spring.springproject.entities.Producer;
import com.spring.springproject.entities.Technique;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.service.interfaces.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TechniqueServiceImpl implements TechniqueService {

    private final ModelMapper modelMapper;
    private final TechniqueRepository repository;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ModelService modelService;
    private final ProducerService producerService;

    @Autowired
    public TechniqueServiceImpl(ModelMapper modelMapper, TechniqueRepository repository,
                                CategoryService categoryService, StoreService storeService,
                                ModelService modelService, ProducerService producerService) {
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.modelService = modelService;
        this.producerService = producerService;
    }


    @Override
    public Set<TechniqueDto> findAll() {
        return repository.findAll()
                .stream()
                .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public TechniqueDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public TechniqueDto save(TechniqueDto object) {
        Technique technique = modelMapper.map(object, Technique.class);
        repository.saveTechnique(technique);
        return object;
    }

    @Override
    @Transactional
    public void update(TechniqueDto object) {
        repository.update(modelMapper.map(object, Technique.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }


    @Override
    @Transactional
    public void update(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes, Integer id) {
        TechniqueDto techniqueDto = findById(id);
        if (techniqueDto != null) {
            setParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        }
        Technique technique = modelMapper.map(techniqueDto, Technique.class);
        repository.update(technique);
        technique.getStoreList().forEach(store->{
                    repository.saveStoreTech(store.getId(), technique.getId());
                }
        );
    }

    @Override
    @Transactional
    public TechniqueDto save(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes) {
        TechniqueDto techniqueDto = new TechniqueDto();
        techniqueDto.setStoreList(new HashSet<>());
        setParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        Technique technique = modelMapper.map(techniqueDto, Technique.class);
        repository.saveTechnique(technique);
        technique.getStoreList().forEach(store->{
                    repository.saveStoreTech(store.getId(), technique.getId());
                }
        );

        return techniqueDto;
    }

    @Override
    public Page<TechniqueDto> findAll(Pageable pageable, Double startPrice, Double endPrice) {
        startPrice = startPrice == null ? 0 : startPrice;
        endPrice = endPrice == null ? 9999999 : endPrice;
        Page<Technique> techniquesPage = repository.findAll(Technique.builder()
                        .producer(Producer.builder().name("").build())
                        .model(Model.builder().name("").build())
                        .category(Category.builder().name("").build())
                        .build(),
                startPrice, endPrice, pageable);
        List<TechniqueDto> techniqueDtoList = techniquesPage
                .stream()
                .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                .toList();
        return new PageImpl<>(techniqueDtoList, pageable, techniquesPage.getTotalElements());
    }

    @Override
    public void saveDataToJsonFile() {
        String filePath = "data.json";
        List<Technique> entities = repository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filePath), entities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importDataFromJson(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Technique[] entities = objectMapper.readValue(file.getBytes(), Technique[].class);
        List<Technique> entityList = Arrays.asList(entities);
        entityList.stream().forEach(technique -> repository.saveTechnique(technique));
    }


    private void setParams(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes, TechniqueDto techniqueDto) {
        techniqueDto.setProducer(producerService.findById(producerId));
        techniqueDto.setModel(modelService.findById(modelId));
        techniqueDto.setCategory(categoryService.findById(categoryId));
        techniqueDto.setPrice(price);
        techniqueDto.getStoreList().removeAll(techniqueDto.getStoreList());
        if (storeIdes != null) {
            Arrays.stream(storeIdes).forEach(storeId -> techniqueDto.getStoreList().add(storeService.findById(storeId)));
        }
    }
}
