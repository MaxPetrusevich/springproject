package com.spring.springproject.service.impl;

import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.entities.Technique;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.service.interfaces.TechniqueService;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TechniqueServiceImpl implements TechniqueService {

    private final ModelMapper modelMapper;
    private final TechniqueRepository repository;

    @Autowired
    public TechniqueServiceImpl(ModelMapper modelMapper, TechniqueRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
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
}
