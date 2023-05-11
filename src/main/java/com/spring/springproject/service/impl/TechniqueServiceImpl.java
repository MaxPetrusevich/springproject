package com.spring.springproject.service.impl;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.entities.Technique;
import com.spring.springproject.service.interfaces.*;
import com.spring.springproject.specifications.TechniqueSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.TechniqueRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Page<TechniqueDto> findAll(Pageable pageable, Double startPrice, Double endPrice) {
        if (startPrice == null && endPrice == null) {
           Page<Technique> techniquesPage = repository.findAll(pageable);
            List<TechniqueDto> techniqueDtoList = techniquesPage
                    .stream()
                    .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                    .toList();
            return new PageImpl<>(techniqueDtoList, pageable, techniquesPage.getTotalElements());
        } else {
            Page<Technique> techniquesPage = repository.findAll(TechniqueSpecification.searchTechnique(startPrice, endPrice),pageable);
            List<TechniqueDto> techniqueDtoList = techniquesPage
                    .stream()
                    .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                    .toList();
            return new PageImpl<>(techniqueDtoList, pageable, techniquesPage.getTotalElements());
        }
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
