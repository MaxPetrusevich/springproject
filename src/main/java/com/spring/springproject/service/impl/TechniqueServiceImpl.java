package com.spring.springproject.service.impl;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.entities.Technique;
import com.spring.springproject.service.interfaces.*;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.TechniqueRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TechniqueServiceImpl implements TechniqueService {

    private final ModelMapper modelMapper;
    private final TechniqueRepository repository;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ModelService modelService;
    private final ProducerService producerService;


    @Autowired
    public TechniqueServiceImpl(ModelMapper modelMapper, TechniqueRepository repository, CategoryService categoryService, StoreService storeService, ModelService modelService, ProducerService producerService) {
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.modelService = modelService;
        this.producerService = producerService;
    }

    @Override
    public Set<TechniqueDto> findAll() {
        Set<TechniqueDto> techniqueDtoSet = new HashSet<>();
        for (Technique technique :
                repository.findAll()) {
            techniqueDtoSet.add(modelMapper.map(technique, TechniqueDto.class));
        }
        return techniqueDtoSet;
    }

    @Override
    public TechniqueDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), TechniqueDto.class);
    }

    @Override
    public TechniqueDto save(TechniqueDto object) {
        Technique technique = modelMapper.map(object, Technique.class);
        technique = repository.save(technique);
        return modelMapper.map(technique, TechniqueDto.class);
    }

    @Override
    public void update(TechniqueDto object) {
        repository.save(modelMapper.map(object, Technique.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public Set<TechniqueDto> findByPriceBetween(Double startPrice, Double endPrice) {
        Set<TechniqueDto> techniqueDtoSet = new HashSet<>();
        for (Technique technique :
                repository.findByPriceBetween(startPrice, endPrice)) {
            techniqueDtoSet.add(modelMapper.map(technique, TechniqueDto.class));
        }
        return techniqueDtoSet;
    }

    @Override
    public void update(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes, Integer id) {
        TechniqueDto techniqueDto = findById(id);
        if (techniqueDto != null) {
            setParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        }
        repository.save(modelMapper.map(techniqueDto, Technique.class));

    }

    @Override
    public TechniqueDto save(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes) {
        TechniqueDto techniqueDto = new TechniqueDto();
        techniqueDto.setStoreList(new HashSet<>());
        setParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        Technique technique = repository.save(modelMapper.map(techniqueDto, Technique.class));
        return modelMapper.map(technique, TechniqueDto.class);
    }

    private void setParams(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes, TechniqueDto techniqueDto) {
        techniqueDto.setProducer(producerService.findById(producerId));
        techniqueDto.setModel(modelService.findById(modelId));
        techniqueDto.setCategory(categoryService.findById(categoryId));
        techniqueDto.setPrice(price);
        techniqueDto.getStoreList().removeAll(techniqueDto.getStoreList());
        Arrays.stream(storeIdes).forEach(storeId -> techniqueDto.getStoreList().add(storeService.findById(storeId)));
    }
}
